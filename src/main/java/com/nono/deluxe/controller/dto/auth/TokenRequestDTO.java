package com.nono.deluxe.controller.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class TokenRequestDTO {
    @NotNull
    private String grant_type; // authorization_code, refresh_token 본 서버에는 두가지 방법을 사용함
    private String code; // authorization_code 의 경우에 사용
    private String refresh_token; // grant_type 의 경우에 사용
}
