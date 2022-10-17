package com.nono.deluxe.controller.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddUserRequestDTO {
	@NotNull
	@Size(max = 20)
	private String userName;
}
