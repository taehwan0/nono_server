package com.nono.deluxe.auth;

import org.springframework.stereotype.Component;

public interface AuthManager {
    String getToken(String username, long tokenActiveSeconds);
    void isUser(String token);
    String extractToken(String token);
}
