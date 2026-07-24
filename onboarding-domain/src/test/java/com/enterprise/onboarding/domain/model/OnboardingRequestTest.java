package com.enterprise.onboarding.domain.model;

import com.enterprise.onboarding.domain.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OnboardingRequestTest {

    private OnboardingRequest pendingRequest() {
        return OnboardingRequest.builder()
                .id(UUID.randomUUID())
                .candidateName("Jane Doe")
                .candidateEmail("jane@example.com")
                .status(OnboardingStatus.PENDING_APPROVAL)
                .build();
    }

    @Test
    void approve_fromPendingApproval_movesToApproved() {
        OnboardingRequest request = pendingRequest();

        request.approve(UUID.randomUUID());

        assertThat(request.getStatus()).isEqualTo(OnboardingStatus.APPROVED);
        assertThat(request.getApprovedAt()).isNotNull();
    }

    @Test
    void approve_whenNotPendingApproval_throws() {
        OnboardingRequest request = pendingRequest();
        request.approve(UUID.randomUUID());

        assertThatThrownBy(() -> request.approve(UUID.randomUUID()))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void reject_fromPendingApproval_movesToRejectedWithReason() {
        OnboardingRequest request = pendingRequest();

        request.reject(UUID.randomUUID(), "Candidate withdrew");

        assertThat(request.getStatus()).isEqualTo(OnboardingStatus.REJECTED);
        assertThat(request.getRejectionReason()).isEqualTo("Candidate withdrew");
    }

    @Test
    void reject_afterAlreadyApproved_throws() {
        OnboardingRequest request = pendingRequest();
        request.approve(UUID.randomUUID());

        assertThatThrownBy(() -> request.reject(UUID.randomUUID(), "too late"))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void fullLifecycle_pendingToCompleted() {
        OnboardingRequest request = pendingRequest();

        request.approve(UUID.randomUUID());
        request.moveToDocumentsPending();
        request.moveToAssetProvisioning();
        request.moveToTasksInProgress();
        request.complete();

        assertThat(request.getStatus()).isEqualTo(OnboardingStatus.COMPLETED);
        assertThat(request.getCompletedAt()).isNotNull();
    }

    @Test
    void moveToTasksInProgress_fromDocumentsPending_isAllowed() {
        OnboardingRequest request = pendingRequest();
        request.approve(UUID.randomUUID());
        request.moveToDocumentsPending();

        request.moveToTasksInProgress();

        assertThat(request.getStatus()).isEqualTo(OnboardingStatus.TASKS_IN_PROGRESS);
    }

    @Test
    void complete_beforeTasksInProgress_throws() {
        OnboardingRequest request = pendingRequest();
        request.approve(UUID.randomUUID());

        assertThatThrownBy(request::complete)
                .isInstanceOf(InvalidStateTransitionException.class);
    }
}
