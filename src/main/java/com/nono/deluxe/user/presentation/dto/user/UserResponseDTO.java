package com.nono.deluxe.user.presentation.dto.user;

import com.nono.deluxe.user.domain.Role;
import com.nono.deluxe.user.domain.User;
import lombok.Data;

@Data
public class UserResponseDTO {

    private long userId;
    private String email;
    private String userName;
    private Role role;
    private boolean active;

    public UserResponseDTO(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.userName = user.getName();
        this.role = user.getRole();
        this.active = user.isActive();
    }
}
