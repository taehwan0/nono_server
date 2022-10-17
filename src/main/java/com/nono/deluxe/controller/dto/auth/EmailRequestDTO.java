package com.nono.deluxe.controller.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailRequestDTO {
	@Email
	private String email;

	@NotNull
	private String type; // JOIN, REISSUE
}
