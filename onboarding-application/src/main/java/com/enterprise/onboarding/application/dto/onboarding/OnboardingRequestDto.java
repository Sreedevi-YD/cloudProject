package com.enterprise.onboarding.application.dto.onboarding;

import com.enterprise.onboarding.domain.model.OnboardingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OnboardingRequestDto(
        UUID id,
        String candidateName,
        String candidateEmail,
        String designation,
        String department,
        LocalDate proposedJoiningDate,
        UUID hiringManagerId,
        UUID employeeId,
        OnboardingStatus status,
        String rejectionReason,
        Instant createdAt,
        Instant approvedAt,
        Instant completedAt
) {
}
