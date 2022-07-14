package com.nono.deluxe.domain.user;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    STRANGER("ROLE_STRANGER"),
    PARTICIPANT("ROLE_PARTICIPANT"),
    MANAGER("ROLE_MANAGER"),
    ADMIN("ROLE_ADMIN");

    private final String key;
}
