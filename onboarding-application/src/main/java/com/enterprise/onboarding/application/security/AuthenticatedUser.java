package com.enterprise.onboarding.application.security;

import java.util.Set;
import java.util.UUID;

/**
 * Shared identity type set as the Spring Security principal by the infrastructure JWT filter and
 * read by presentation controllers — lets both depend only on the application module, never on
 * each other.
 */
public record AuthenticatedUser(UUID userId, String username, Set<String> roles) {
}
