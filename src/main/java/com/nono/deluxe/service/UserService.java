package com.nono.deluxe.service;

import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.user.AddUserRequestDTO;
import com.nono.deluxe.controller.dto.user.GetUserListResponseDTO;
import com.nono.deluxe.controller.dto.user.UpdateUserRequestDTO;
import com.nono.deluxe.controller.dto.user.UserResponseDTO;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public User createAdmin(String temp) {
        User user = User.builder()
                .name(temp)
                .email(temp)
                .password(temp)
                .build();
        return userRepository.save(user);
    }

    public User createParticipant(String temp) {
        User user = User.builder()
                .name(temp)
                .email(temp)
                .password(temp)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public UserResponseDTO addUser(AddUserRequestDTO userRequestDTO) {
        User user = User.builder()
                .name(userRequestDTO.getUserName())
                .email(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .role(Role.ROLE_PARTICIPANT)
                .build();
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public GetUserListResponseDTO readUserList(String query,
                                               String column,
                                               String order,
                                               int size,
                                               int page) {
        String pageColumn = column;
        if (column.equals("userName")) {
            pageColumn = "name";
        }
        Pageable pageable = PageRequest.of(page,
                size,
                Sort.by(new Sort.Order(
                        Sort.Direction.valueOf(
                                order.toUpperCase(Locale.ROOT)
                        ),
                        pageColumn)
                )
        );

        Page<User> userPage = userRepository.readUserList(query, pageable);
        return new GetUserListResponseDTO(userPage);
    }

    public UserResponseDTO getUserInfo(long userCode) {
        User user = userRepository.findById(userCode)
                .orElseThrow(() -> new RuntimeException("Not exist data."));

        return new UserResponseDTO(user);
    }

    public UserResponseDTO updateUser(long userCode, UpdateUserRequestDTO userRequestDTO) {
        User updateUser = userRepository.findById(userCode)
                .orElseThrow(() -> new RuntimeException("User: Not found user"));

        updateUser.update(userRequestDTO);
        userRepository.save(updateUser);
        return new UserResponseDTO(updateUser);
    }

    public MessageResponseDTO deleteUser(long userCode) {
        User user = userRepository.findById(userCode)
                .orElseThrow(() -> new RuntimeException("User: Not found user"));

        user.delete();

        return new MessageResponseDTO(true, "Deleted");
    }
}
