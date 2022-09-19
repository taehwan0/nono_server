package com.nono.deluxe.controller;

import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.auth.EmailRequestDTO;
import com.nono.deluxe.controller.dto.auth.LoginRequestDTO;
import com.nono.deluxe.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    public ResponseEntity<MessageResponseDTO> checkDuplicateEmail(@Validated @RequestBody EmailRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = authService.checkDuplicateEmail(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/email/check")
    public ResponseEntity<MessageResponseDTO> postCheckEmail(@Validated @RequestBody EmailRequestDTO requestDTO) {
        try {
            MessageResponseDTO responseDTO = authService.checkEmail(requestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
