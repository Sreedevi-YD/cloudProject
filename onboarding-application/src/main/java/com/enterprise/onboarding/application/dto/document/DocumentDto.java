package com.enterprise.onboarding.application.dto.document;

import com.enterprise.onboarding.domain.model.DocumentType;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        UUID onboardingRequestId,
        UUID employeeId,
        DocumentType documentType,
        String fileName,
        String contentType,
        long fileSizeBytes,
        Instant uploadedAt
) {
}
