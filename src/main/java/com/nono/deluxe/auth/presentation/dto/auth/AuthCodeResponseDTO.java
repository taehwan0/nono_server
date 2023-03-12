package com.nono.deluxe.auth.presentation.dto.auth;

import com.nono.deluxe.auth.domain.AuthCode;
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
