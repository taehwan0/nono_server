package com.nono.deluxe.service;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> readUserList() {
        return userRepository.findAll();
    }

    @Transactional
    public User createAdmin(String temp) {
        User user = User.builder()
                .name(temp)
                .email(temp)
                .password(temp)
                .role(Role.ROLE_ADMIN)
                .build();
        return userRepository.save(user);
    }

    public User createParticipant(String temp) {
        User user = User.builder()
                .name(temp)
                .email(temp)
                .password(temp)
                .role(Role.ROLE_PARTICIPANT)
                .build();
        return userRepository.save(user);
    }
}
