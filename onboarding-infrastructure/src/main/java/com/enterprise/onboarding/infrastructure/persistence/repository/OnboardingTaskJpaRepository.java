package com.enterprise.onboarding.infrastructure.persistence.repository;

import com.enterprise.onboarding.domain.model.OwningDepartment;
import com.enterprise.onboarding.infrastructure.persistence.entity.OnboardingTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OnboardingTaskJpaRepository extends JpaRepository<OnboardingTaskEntity, UUID> {
    List<OnboardingTaskEntity> findByOnboardingRequestId(UUID onboardingRequestId);
    List<OnboardingTaskEntity> findByOwningDepartment(OwningDepartment owningDepartment);
    List<OnboardingTaskEntity> findByAssignedToUserId(UUID assignedToUserId);
}
