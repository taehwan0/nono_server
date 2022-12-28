package com.nono.deluxe.presentation.dto.user;

import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.domain.user.User;
import lombok.Data;

@Data
public class UserResponseDTO {

    private long id;
    private String email;
    private String name;
    private Role role;
    private boolean active;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
        this.active = user.isActive();
    }
}
