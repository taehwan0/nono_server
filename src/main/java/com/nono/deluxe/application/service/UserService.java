package com.nono.deluxe.application.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.application.client.MailClient;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final MailClient mailClient;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Transactional
    public UserResponseDTO createParticipant(CreateParticipantRequestDTO createParticipantRequestDTO) {
        User user = User.createParticipant(createParticipantRequestDTO.getUserName());

        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public GetUserListResponseDTO getUserList(String query, String column, String order, int size, int page) {
        if (column.equals("userName")) {
            column = "name";
        }

        Pageable pageable =
            PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        Page<User> userPage = userRepository.findPageByName(query, pageable);

        return new GetUserListResponseDTO(userPage);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUser(long userCode) {
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

        return new MessageResponseDTO(true, "Deleted");
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

            return new MessageResponseDTO(true, "success");
        }
        return new MessageResponseDTO(false, "fail");
    }

    @Transactional
    public MessageResponseDTO deleteMe(long userId, DeleteMeRequestDTO deleteMeRequestDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Not Found User"));

        if (encoder.matches(deleteMeRequestDTO.getPassword(), user.getPassword())) {
            user.delete();
            return new MessageResponseDTO(true, "success");
        }
        return new MessageResponseDTO(false, "fail");
    }
}
