package com.nono.deluxe.auth.presentation.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponseDTO {

    private String accessToken;
    private String refreshToken;
}
