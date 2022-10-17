package com.nono.deluxe.controller.dto.auth;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthCodeRequestDTO {
	@NotNull
	private String code;
}
