package com.nono.deluxe.presentation.dto.user;

import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePasswordRequestDTO {

    private String password;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_+=])[A-Za-z\\d!@#$%^&*()]{8,}$")
    private String newPassword;
}
