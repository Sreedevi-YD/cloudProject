package com.enterprise.onboarding.infrastructure.persistence.repository;

import com.enterprise.onboarding.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, UUID> {
    List<DocumentEntity> findByOnboardingRequestId(UUID onboardingRequestId);
    List<DocumentEntity> findByEmployeeId(UUID employeeId);
}
