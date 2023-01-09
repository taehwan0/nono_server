package com.nono.deluxe.presentation.dto.auth;

import lombok.Data;

@Data
public class TokenResponseDTO {

    private String token_type; // 토큰 타입 (본 서버에서는 bearer 고정)

    private String access_token; // access token
    private long expires_in; // access token expire time

    private String refresh_token; // refresh token
    private long refresh_token_expires_in; // refresh token expire time

    public TokenResponseDTO(String token_type,
        String access_token,
        long expires_in,
        String refresh_token,
        long refresh_token_expires_in) {
        this.token_type = token_type;
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.refresh_token_expires_in = refresh_token_expires_in;
    }
}
