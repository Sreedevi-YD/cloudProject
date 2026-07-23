package com.enterprise.onboarding.domain.repository;

import com.enterprise.onboarding.domain.model.OnboardingTask;
import com.enterprise.onboarding.domain.model.OwningDepartment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingTaskRepository {
    OnboardingTask save(OnboardingTask task);
    List<OnboardingTask> saveAll(List<OnboardingTask> tasks);
    Optional<OnboardingTask> findById(UUID id);
    List<OnboardingTask> findByOnboardingRequestId(UUID onboardingRequestId);
    List<OnboardingTask> findByOwningDepartment(OwningDepartment department);
    List<OnboardingTask> findByAssignedToUserId(UUID userId);
}
