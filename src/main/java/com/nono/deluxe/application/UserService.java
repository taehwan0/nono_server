package com.nono.deluxe.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.user.CreateParticipantRequestDTO;
import com.nono.deluxe.presentation.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.presentation.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.presentation.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

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

        Page<User> userPage = userRepository.readUserList(query, pageable);

        return new GetUserListResponseDTO(userPage);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserInfo(long userCode) {
        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new NotFoundException("Not exist data."));

        return new UserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(long userCode, UpdateUserRequestDTO requestDTO) {
        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new NotFoundException("User: Not found user"));

        user.update(requestDTO);

        return new UserResponseDTO(user);
    }

    @Transactional
    public MessageResponseDTO deleteUser(long userCode) {
        User user = userRepository.findById(userCode)
            .orElseThrow(() -> new NotFoundException("User: Not found user"));

        user.delete();

        return new MessageResponseDTO(true, "Deleted");
    }
}
