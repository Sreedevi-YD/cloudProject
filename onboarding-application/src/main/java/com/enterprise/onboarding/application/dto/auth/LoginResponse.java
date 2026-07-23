package com.enterprise.onboarding.application.dto.auth;

import java.util.Set;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        Set<String> roles
) {
    public LoginResponse(String accessToken, long expiresInSeconds, String username, Set<String> roles) {
        this(accessToken, "Bearer", expiresInSeconds, username, roles);
    }
}
