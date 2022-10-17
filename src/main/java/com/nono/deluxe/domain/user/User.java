package com.nono.deluxe.domain.user;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.nono.deluxe.controller.dto.user.UpdateUserRequestDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, length = 20)
	private String name;

	/*
	ROLE PARTICIPANT 의 경우 임의의 고유 값을 할당하고, 사용하지 않도록 할 것
	또한 회원가입 로직을 통해 USER 생성 시 이메일 형식의 입력만 받도록 할 것
	 */
	@Column(nullable = false, unique = true)
	private String email;

	/*
	ROLE PARTICIPANT 의 경우 임의의 고유 값을 할당하고, 사용하지 않도록 할 것
	또한 회원가입 로직을 통해 USER 생성 시 공백을 제외하는 등의 형식을 입력 받도록 할 것 -> 로그인 시에도 적용
	암호화 하여 저장할 것
	 */
	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(columnDefinition = "tinyint(1) default 0")
	private boolean active;

	@Column(columnDefinition = "tinyint(1) default 0")
	private boolean deleted;

	@Builder
	public User(String name, String email, String password, Role role, boolean active) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.active = active;
		this.deleted = false;
	}

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}

	public void delete() {
		this.deleted = true;
		this.active = false;
		this.name = UUID.randomUUID().toString().substring(0, 18);
	}

	public void changeActivation(boolean isActive) {
		this.active = isActive;
	}

	public void update(UpdateUserRequestDTO userRequestDTO) {
		this.name = userRequestDTO.getUserName();
		this.email = userRequestDTO.getEmail();
		this.role = userRequestDTO.getRole();
		this.active = userRequestDTO.isActive();
		this.deleted = userRequestDTO.isDeleted();
	}
}
