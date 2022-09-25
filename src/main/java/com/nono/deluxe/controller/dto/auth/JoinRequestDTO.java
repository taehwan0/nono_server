package com.nono.deluxe.controller.dto.auth;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
public class JoinRequestDTO {
    /**
     * Validation 추가 필요함
     */
    @Email
    private String email;

    private String password;

    private String name;

    private String code;

    public User toEntity() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();
    }
}
