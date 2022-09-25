package com.nono.deluxe.controller.dto.auth;

import com.nono.deluxe.domain.authcode.AuthCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthCodeResponseDTO {
    private String code;

    public AuthCodeResponseDTO(AuthCode authCode) {
        this.code = authCode.getCode();
    }
}
