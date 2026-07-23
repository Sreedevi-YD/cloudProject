package com.enterprise.onboarding.application.dto.asset;

import com.enterprise.onboarding.domain.model.AssetStatus;
import com.enterprise.onboarding.domain.model.AssetType;

import java.time.Instant;
import java.util.UUID;

public record AssetDto(
        UUID id,
        UUID onboardingRequestId,
        UUID employeeId,
        AssetType assetType,
        String assetTag,
        AssetStatus status,
        Instant assignedAt,
        Instant returnedAt
) {
}
