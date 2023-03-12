package com.nono.deluxe.auth.presentation.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyEmailRequestDTO {

    @Email
    private String email;
    @NotNull
    private String code;
}
