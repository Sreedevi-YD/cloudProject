package com.enterprise.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Authentication identity. Separate from {@link Employee} so IT/HR/Admin accounts don't require an employee profile. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private boolean enabled;

    @Builder.Default
    private Set<RoleName> roles = new HashSet<>();

    private UUID employeeId;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean hasRole(RoleName role) {
        return roles.contains(role);
    }
}
