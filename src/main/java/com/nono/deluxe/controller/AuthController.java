package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.auth.*;
import com.nono.deluxe.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/code")
    public ResponseEntity<AuthCodeResponseDTO> login(@RequestBody CreateAuthCodeRequestDTO requestDTO) {
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
    public ResponseEntity<AuthCodeResponseDTO> createAuthCode(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "userCode") long userCode) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isAdmin(jwt)) {
        }
        AuthCodeResponseDTO responseDTO = authService.createAuthCode(userCode);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> createToken(@RequestBody TokenRequestDTO requestDTO) {
        TokenResponseDTO responseDTO = authService.createToken(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
