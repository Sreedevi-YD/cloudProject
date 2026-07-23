package com.enterprise.onboarding.presentation.security;

import com.enterprise.onboarding.application.security.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/** Reads the {@link AuthenticatedUser} the JWT filter placed on the security context. */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static AuthenticatedUser get() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AuthenticatedUser user) {
            return user;
        }
        throw new IllegalStateException("No authenticated user in the current security context");
    }

    public static UUID id() {
        return get().userId();
    }
}
