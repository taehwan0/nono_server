package com.nono.deluxe.controller.dto.auth;

import com.nono.deluxe.domain.logincode.LoginCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginCodeResponseDTO {
    private String code;

    public LoginCodeResponseDTO(LoginCode loginCode) {
        this.code = loginCode.getCode();
    }
}
