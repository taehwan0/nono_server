package com.nono.deluxe.presentation.dto.user;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
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
