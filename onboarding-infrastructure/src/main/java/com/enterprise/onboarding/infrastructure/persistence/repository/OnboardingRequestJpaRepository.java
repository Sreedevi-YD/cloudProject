package com.enterprise.onboarding.infrastructure.persistence.repository;

import com.enterprise.onboarding.domain.model.OnboardingStatus;
import com.enterprise.onboarding.infrastructure.persistence.entity.OnboardingRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface OnboardingRequestJpaRepository
        extends JpaRepository<OnboardingRequestEntity, UUID>, JpaSpecificationExecutor<OnboardingRequestEntity> {

    List<OnboardingRequestEntity> findByHiringManagerId(UUID hiringManagerId);
    List<OnboardingRequestEntity> findByStatus(OnboardingStatus status);
}
