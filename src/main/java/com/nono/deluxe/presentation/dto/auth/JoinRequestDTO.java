package com.nono.deluxe.presentation.dto.auth;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinRequestDTO {

    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_+=])[A-Za-z\\d!@#$%^&*()\\-_+=]{8,}$")
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
            .active(false)
            .build();
    }
}
