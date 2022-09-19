package com.nono.deluxe.domain.user;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum Role {
    ROLE_PARTICIPANT,
    ROLE_MANAGER,
    ROLE_ADMIN;
}
