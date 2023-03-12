package com.nono.deluxe.auth.presentation.dto.auth;

import com.nono.deluxe.user.domain.User;
import lombok.Data;

@Data
public class JoinResponseDTO {

    private long userCode;
    private String email;
    private String name;

    public JoinResponseDTO(User user) {
        this.userCode = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
