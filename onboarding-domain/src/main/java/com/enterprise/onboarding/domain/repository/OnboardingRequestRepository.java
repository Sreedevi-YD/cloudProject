package com.enterprise.onboarding.domain.repository;

import com.enterprise.onboarding.domain.model.OnboardingRequest;
import com.enterprise.onboarding.domain.model.OnboardingStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingRequestRepository {
    OnboardingRequest save(OnboardingRequest request);
    Optional<OnboardingRequest> findById(UUID id);
    List<OnboardingRequest> findByHiringManagerId(UUID managerId);
    List<OnboardingRequest> findByStatus(OnboardingStatus status);
    PageResult<OnboardingRequest> search(String query, OnboardingStatus status, PageRequest pageRequest);
}
