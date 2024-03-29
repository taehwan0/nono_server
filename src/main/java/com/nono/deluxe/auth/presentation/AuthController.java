package com.nono.deluxe.auth.presentation;

import com.nono.deluxe.auth.application.AuthService;
import com.nono.deluxe.auth.presentation.dto.auth.AuthCodeResponseDTO;
import com.nono.deluxe.auth.presentation.dto.auth.CreateAuthCodeRequestDTO;
import com.nono.deluxe.auth.presentation.dto.auth.EmailRequestDTO;
import com.nono.deluxe.auth.presentation.dto.auth.JoinRequestDTO;
import com.nono.deluxe.auth.presentation.dto.auth.JoinResponseDTO;
import com.nono.deluxe.auth.presentation.dto.auth.ReissueUserRequestDTO;
import com.nono.deluxe.auth.presentation.dto.auth.TokenRequestDTO;
import com.nono.deluxe.auth.presentation.dto.auth.TokenResponseDTO;
import com.nono.deluxe.auth.presentation.dto.auth.VerifyEmailRequestDTO;
import com.nono.deluxe.common.configuration.annotation.Auth;
import com.nono.deluxe.common.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/code")
    public ResponseEntity<AuthCodeResponseDTO> login(@Validated @RequestBody CreateAuthCodeRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.createAuthCodeBySignIn(requestDTO));
    }

    @PostMapping("/join")
    public ResponseEntity<JoinResponseDTO> joinUser(@Validated @RequestBody JoinRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.joinUser(requestDTO));
    }

    @PostMapping("/email/duplicate")
    public ResponseEntity<MessageResponseDTO> checkDuplicateEmail(@Validated @RequestBody EmailRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.checkDuplicateEmail(requestDTO));
    }

    @PostMapping("/email/check")
    public ResponseEntity<MessageResponseDTO> postCheckEmail(@Validated @RequestBody EmailRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.checkEmail(requestDTO));
    }

    @PostMapping("/email/verify")
    public ResponseEntity<MessageResponseDTO> verifyEmail(@Validated @RequestBody VerifyEmailRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.verifyEmail(requestDTO));
    }

    @PostMapping("/reissue")
    public ResponseEntity<MessageResponseDTO> reissueUser(@Validated @RequestBody ReissueUserRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.reissueUser(requestDTO));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @PostMapping("/code/{userCode}")
    public ResponseEntity<AuthCodeResponseDTO> createAuthCode(@PathVariable(name = "userCode") long userCode) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.createAuthCodeOfParticipant(userCode));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> createToken(@Validated @RequestBody TokenRequestDTO requestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.createToken(requestDTO));
    }
}
