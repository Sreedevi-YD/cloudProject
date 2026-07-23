package com.enterprise.onboarding.application.dto.task;

import com.enterprise.onboarding.domain.model.OwningDepartment;
import com.enterprise.onboarding.domain.model.TaskStatus;

import java.time.Instant;
import java.util.UUID;

public record TaskDto(
        UUID id,
        UUID onboardingRequestId,
        String title,
        String description,
        OwningDepartment owningDepartment,
        UUID assignedToUserId,
        TaskStatus status,
        Instant createdAt,
        Instant completedAt
) {
}
