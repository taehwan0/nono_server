package com.nono.deluxe.controller.dto.user;

import com.nono.deluxe.domain.user.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateUserRequestDTO {
    private String userName;
    private String email;
    private Role role;
    private boolean active;
    private boolean deleted;
}
