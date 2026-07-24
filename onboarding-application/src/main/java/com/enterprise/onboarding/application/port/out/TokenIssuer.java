package com.enterprise.onboarding.application.port.out;

import java.util.Set;
import java.util.UUID;

public interface TokenIssuer {

    IssuedToken issue(UUID userId, String username, Set<String> roles);

    record IssuedToken(String token, long expiresInSeconds) {
    }
}
