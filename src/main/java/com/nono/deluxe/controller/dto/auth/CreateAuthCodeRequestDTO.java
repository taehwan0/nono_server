package com.nono.deluxe.controller.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateAuthCodeRequestDTO {
    private String email;
    private String password;
}
