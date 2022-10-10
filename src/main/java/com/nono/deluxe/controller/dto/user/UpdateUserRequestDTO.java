package com.nono.deluxe.controller.dto.user;

import com.nono.deluxe.domain.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
