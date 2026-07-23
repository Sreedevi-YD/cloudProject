package com.enterprise.onboarding.application.event;

import com.enterprise.onboarding.domain.model.AssetType;

import java.util.UUID;

public record AssetAssignedEvent(UUID assetId, UUID onboardingRequestId, UUID employeeId, AssetType assetType) {
}
