package com.nono.deluxe.service;

import com.nono.deluxe.auth.AuthManager;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthManager authManager;

    public String loginUser(String email, String password, long tokenActiveSeconds) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Not Found User"));
        return authManager.getToken(user.getName(), tokenActiveSeconds);
    }
}
