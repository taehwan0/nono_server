package com.nono.deluxe.controller.dto.auth;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
public class JoinRequestDTO {
    @Email
    private String email;

    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
    private String password;

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
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
