package com.nono.deluxe.controller.auth;

import com.nono.deluxe.auth.AuthManager;
import com.nono.deluxe.controller.auth.dto.LoginRequestDto;
import com.nono.deluxe.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.loginUser(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                loginRequestDto.getTokenActiveSeconds()
        );
    }
}
