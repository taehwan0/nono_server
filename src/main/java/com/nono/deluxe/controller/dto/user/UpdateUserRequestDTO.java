package com.nono.deluxe.controller.dto.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.nono.deluxe.domain.user.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateUserRequestDTO {
	@NotNull
	@Size(max = 20)
	private String userName;
	private String email;
	@NotNull
	private Role role;
	@NotNull
	private boolean active;
	private boolean deleted;
}
