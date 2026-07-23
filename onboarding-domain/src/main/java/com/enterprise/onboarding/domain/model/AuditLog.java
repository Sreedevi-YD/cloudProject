package com.enterprise.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/** Immutable record of a significant activity. Written once, never updated. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private UUID id;
    private String entityType;
    private UUID entityId;
    private AuditAction action;
    private UUID performedByUserId;
    private String details;
    private Instant occurredAt;
}
