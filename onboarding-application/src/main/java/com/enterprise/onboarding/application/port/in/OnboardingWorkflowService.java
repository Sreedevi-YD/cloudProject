package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.onboarding.CreateOnboardingRequest;
import com.enterprise.onboarding.application.dto.onboarding.OnboardingRequestDto;
import com.enterprise.onboarding.domain.model.OnboardingStatus;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;

import java.util.UUID;

/** Orchestrates workflow steps 1-2 and 6 (creation, approval/rejection, completion) of the onboarding process. */
public interface OnboardingWorkflowService {

    OnboardingRequestDto createRequest(CreateOnboardingRequest request, UUID hrUserId);

    OnboardingRequestDto approveRequest(UUID requestId, UUID managerUserId);

    OnboardingRequestDto rejectRequest(UUID requestId, UUID managerUserId, String reason);

    OnboardingRequestDto getRequest(UUID requestId);

    PageResult<OnboardingRequestDto> search(String query, OnboardingStatus status, PageRequest pageRequest);

    /** Called internally once every task for the request reaches COMPLETED (step 6). */
    void completeIfAllTasksDone(UUID requestId);
}
