package com.enterprise.onboarding.application.dto.audit;

import com.enterprise.onboarding.domain.model.AuditAction;

import java.time.Instant;
import java.util.UUID;

public record AuditLogDto(
        UUID id,
        String entityType,
        UUID entityId,
        AuditAction action,
        UUID performedByUserId,
        String details,
        Instant occurredAt
) {
}
