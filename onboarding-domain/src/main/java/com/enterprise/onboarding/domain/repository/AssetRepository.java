package com.enterprise.onboarding.domain.repository;

import com.enterprise.onboarding.domain.model.Asset;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {
    Asset save(Asset asset);
    Optional<Asset> findById(UUID id);
    List<Asset> findByOnboardingRequestId(UUID onboardingRequestId);
    List<Asset> findByEmployeeId(UUID employeeId);
}
