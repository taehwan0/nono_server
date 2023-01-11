package com.nono.deluxe.application.service;

import com.nono.deluxe.application.client.MailClient;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import com.nono.deluxe.exception.NotFoundException;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.user.CreateParticipantRequestDTO;
import com.nono.deluxe.presentation.dto.user.DeleteMeRequestDTO;
import com.nono.deluxe.presentation.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.presentation.dto.user.UpdatePasswordRequestDTO;
import com.nono.deluxe.presentation.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.presentation.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final MailClient mailClient;
    private final BCryptPasswordEncoder encoder;

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO createParticipant(CreateParticipantRequestDTO createParticipantRequestDTO) {
        User user = User.createParticipant(createParticipantRequestDTO.getUserName());

        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public GetUserListResponseDTO getUserList(PageRequest pageRequest, String query) {

        Page<User> userPage = userRepository.findPageByName(query, pageRequest);

        return new GetUserListResponseDTO(userPage);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(long userCode) {
        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new NotFoundException("Not exist data."));

        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(long userCode, UpdateUserRequestDTO requestDTO) {
        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new NotFoundException("User: Not found user"));

        user.updateByAdmin(requestDTO.getUserName(), requestDTO.isActive());

        return new UserResponseDTO(user);
    }

    @Transactional
    public MessageResponseDTO deleteParticipant(long userCode) {
        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new NotFoundException("User: Not found user"));

        if (!user.getRole().equals(Role.ROLE_PARTICIPANT)) {
            throw new RuntimeException("참여자의 정보만 수정 가능합니다.");
        }

        user.delete();

        return MessageResponseDTO.ofSuccess("deleted");
    }

    @Transactional
    public UserResponseDTO updateMe(long userId, UpdateUserRequestDTO updateUserRequestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));

        user.update(updateUserRequestDTO.getUserName(), updateUserRequestDTO.isActive());

        return new UserResponseDTO(user);
    }

    @Transactional
    public MessageResponseDTO updatePassword(long userId, UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));

        if (encoder.matches(updatePasswordRequestDTO.getPassword(), user.getPassword())) {
            user.updatePassword(updatePasswordRequestDTO.getNewPassword());
            user.encodePassword(encoder);
            mailClient.postUpdatePasswordMail(user.getEmail());

            return MessageResponseDTO.ofSuccess("success");
        }
        return MessageResponseDTO.ofFail("fail");
    }

    @Transactional
    public MessageResponseDTO deleteMe(long userId, DeleteMeRequestDTO deleteMeRequestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));

        if (encoder.matches(deleteMeRequestDTO.getPassword(), user.getPassword())) {
            user.delete();
            return MessageResponseDTO.ofSuccess("success");
        }
        return MessageResponseDTO.ofFail("fail");
    }
}
