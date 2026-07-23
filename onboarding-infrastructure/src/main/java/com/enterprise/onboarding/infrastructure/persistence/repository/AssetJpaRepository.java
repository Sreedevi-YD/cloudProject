package com.enterprise.onboarding.infrastructure.persistence.repository;

import com.enterprise.onboarding.infrastructure.persistence.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssetJpaRepository extends JpaRepository<AssetEntity, UUID> {
    List<AssetEntity> findByOnboardingRequestId(UUID onboardingRequestId);
    List<AssetEntity> findByEmployeeId(UUID employeeId);
}
