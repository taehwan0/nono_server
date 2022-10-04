package com.nono.deluxe.controller.dto.user;

import com.nono.deluxe.domain.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class UpdateUserRequestDTO {
    @NotBlank
    private String userName;
    private String email;
    @NotBlank
    private Role role;
    @NotBlank
    private boolean active;
    private boolean deleted;
}
