package com.enterprise.onboarding.domain.model;

import com.enterprise.onboarding.domain.exception.InvalidStateTransitionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingTask {

    private UUID id;
    private UUID onboardingRequestId;
    private String title;
    private String description;
    private OwningDepartment owningDepartment;
    private UUID assignedToUserId;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    public void markInProgress() {
        this.status = TaskStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();
    }

    public void markCompleted() {
        if (this.status == TaskStatus.COMPLETED) {
            throw new InvalidStateTransitionException("Task " + id + " is already completed");
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.updatedAt = this.completedAt;
    }
}
