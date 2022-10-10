package com.nono.deluxe.controller.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ReissueUserRequestDTO {
    @Email
    private String email;
    @NotNull
    private String code;
}
