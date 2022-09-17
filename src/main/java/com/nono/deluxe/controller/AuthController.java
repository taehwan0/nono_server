package com.nono.deluxe.controller;

import com.nono.deluxe.controller.dto.auth.LoginRequestDTO;
import com.nono.deluxe.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public String login(@RequestBody LoginRequestDTO loginRequestDto) {
        return authService.loginUser(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                loginRequestDto.getTokenActiveSeconds()
        );
    }

    @PostMapping("/join")
    public void joinUser() {
    }

    @PostMapping("/email/duplicate")
    public void checkDuplicateEmail() {
    }

    @PostMapping("/email/check")
    public void postCheckEmail() {
    }

    @PostMapping("/email/verify")
    public void verifyEmail() {
    }

    @PostMapping("/reissue")
    public void reissueUser() {
    }

    @PostMapping("/logincode")
    public void getLoginCode() {
    }

    @PostMapping("/logincode/verify")
    public void verifyLoginCode() {
    }
}
