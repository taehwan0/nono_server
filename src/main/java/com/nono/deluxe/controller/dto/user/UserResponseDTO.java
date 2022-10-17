package com.nono.deluxe.controller.dto.user;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;

import lombok.Data;

@Data
public class UserResponseDTO {
	private long userCode;
	private String email;
	private String userName;
	private Role userType;

	private boolean isActive;

	public UserResponseDTO(User user) {
		this.userCode = user.getId();
		this.email = user.getEmail();
		this.userName = user.getName();
		this.userType = user.getRole();
		this.isActive = user.isActive();
	}
}
