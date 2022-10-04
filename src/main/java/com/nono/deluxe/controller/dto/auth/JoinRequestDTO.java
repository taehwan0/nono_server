package com.nono.deluxe.controller.dto.auth;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class JoinRequestDTO {
    @Email
    private String email;

    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    private String password;

    @NotBlank
    private String name;

    @NotBlank
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
