package com.nono.deluxe.controller.dto.auth;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    private String email;
    private String password;
    private long tokenActiveSeconds;
}
