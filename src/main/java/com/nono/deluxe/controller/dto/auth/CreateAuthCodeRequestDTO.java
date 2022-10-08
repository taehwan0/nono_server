package com.nono.deluxe.controller.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class CreateAuthCodeRequestDTO {
    @Email
    private String email;

    /**
     * password 는 회원가입 시에만 검증함
     */
    @NotNull
    private String password;
}
