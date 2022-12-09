package com.nono.deluxe.presentation;

import com.nono.deluxe.application.AuthService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.auth.AuthCodeResponseDTO;
import com.nono.deluxe.presentation.dto.auth.CreateAuthCodeRequestDTO;
import com.nono.deluxe.presentation.dto.auth.EmailRequestDTO;
import com.nono.deluxe.presentation.dto.auth.JoinRequestDTO;
import com.nono.deluxe.presentation.dto.auth.JoinResponseDTO;
import com.nono.deluxe.presentation.dto.auth.ReissueUserRequestDTO;
import com.nono.deluxe.presentation.dto.auth.TokenRequestDTO;
import com.nono.deluxe.presentation.dto.auth.TokenResponseDTO;
import com.nono.deluxe.presentation.dto.auth.VerifyEmailRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
        AuthCodeResponseDTO responseDTO = authService.createAuthCode(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/join")
    public ResponseEntity<JoinResponseDTO> joinUser(@Validated @RequestBody JoinRequestDTO requestDTO) {
        JoinResponseDTO responseDTO = authService.joinUser(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/email/duplicate")
    public ResponseEntity<MessageResponseDTO> checkDuplicateEmail(@Validated @RequestBody EmailRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = authService.checkDuplicateEmail(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/email/check")
    public ResponseEntity<MessageResponseDTO> postCheckEmail(@Validated @RequestBody EmailRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = authService.checkEmail(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/email/verify")
    public ResponseEntity<MessageResponseDTO> verifyEmail(@Validated @RequestBody VerifyEmailRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = authService.verifyEmail(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /**
     * token reissue 와 path 조정 필요
     *
     * @param requestDTO
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity<MessageResponseDTO> reissueUser(@Validated @RequestBody ReissueUserRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = authService.reissueUser(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/code/{userCode}")
    public ResponseEntity<AuthCodeResponseDTO> createAuthCode(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "userCode") long userCode) {
        authService.validateAdminToken(token);

        AuthCodeResponseDTO responseDTO = authService.createAuthCode(userCode);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> createToken(@Validated @RequestBody TokenRequestDTO requestDTO) {
        TokenResponseDTO responseDTO = authService.createToken(requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
