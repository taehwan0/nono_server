package com.nono.deluxe.controller.dto.auth;

import lombok.Data;

@Data
public class TokenResponseDTO {
	private String token_type; // 토큰 타입 (본 서버에서는 bearer 고정)

	private String access_token; // access token
	private long expires_in; // access token expire time

	private String refresh_token; // refresh token
	private long refresh_token_expires_in; // refresh token expire time
}
