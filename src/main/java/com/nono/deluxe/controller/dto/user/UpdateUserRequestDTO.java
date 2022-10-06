package com.nono.deluxe.controller.dto.user;

import com.nono.deluxe.domain.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
public class UpdateUserRequestDTO {
    @NotBlank
    @Size(max = 20)
    private String userName;
    private String email;
    @NotBlank
    private Role role;
    @NotBlank
    private boolean active;
    private boolean deleted;
}
