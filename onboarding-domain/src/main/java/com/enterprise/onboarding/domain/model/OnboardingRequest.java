package com.enterprise.onboarding.domain.model;

import com.enterprise.onboarding.domain.exception.InvalidStateTransitionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Aggregate root for the onboarding workflow. State transitions are enforced here
 * (not in the service layer) so the workflow rules hold regardless of caller.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingRequest {

    private UUID id;
    private String candidateName;
    private String candidateEmail;
    private String designation;
    private String department;
    private LocalDate proposedJoiningDate;

    private UUID createdByUserId;
    private UUID hiringManagerId;
    private UUID employeeId;

    private OnboardingStatus status;
    private String rejectionReason;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant approvedAt;
    private Instant completedAt;

    public void approve(UUID approverId) {
        requireStatus(OnboardingStatus.PENDING_APPROVAL, "approve");
        this.status = OnboardingStatus.APPROVED;
        this.approvedAt = Instant.now();
        this.updatedAt = this.approvedAt;
    }

    public void reject(UUID approverId, String reason) {
        requireStatus(OnboardingStatus.PENDING_APPROVAL, "reject");
        this.status = OnboardingStatus.REJECTED;
        this.rejectionReason = reason;
        this.updatedAt = Instant.now();
    }

    public void moveToDocumentsPending() {
        requireStatus(OnboardingStatus.APPROVED, "move to documents-pending");
        this.status = OnboardingStatus.DOCUMENTS_PENDING;
        this.updatedAt = Instant.now();
    }

    public void moveToAssetProvisioning() {
        requireStatus(OnboardingStatus.DOCUMENTS_PENDING, "move to asset-provisioning");
        this.status = OnboardingStatus.ASSET_PROVISIONING;
        this.updatedAt = Instant.now();
    }

    public void moveToTasksInProgress() {
        if (this.status != OnboardingStatus.ASSET_PROVISIONING && this.status != OnboardingStatus.DOCUMENTS_PENDING) {
            throw new InvalidStateTransitionException(
                    "Cannot move to TASKS_IN_PROGRESS from " + this.status);
        }
        this.status = OnboardingStatus.TASKS_IN_PROGRESS;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        requireStatus(OnboardingStatus.TASKS_IN_PROGRESS, "complete");
        this.status = OnboardingStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.updatedAt = this.completedAt;
    }

    private void requireStatus(OnboardingStatus expected, String action) {
        if (this.status != expected) {
            throw new InvalidStateTransitionException(
                    "Cannot " + action + " request " + id + " in status " + this.status
                            + " (expected " + expected + ")");
        }
    }
}
