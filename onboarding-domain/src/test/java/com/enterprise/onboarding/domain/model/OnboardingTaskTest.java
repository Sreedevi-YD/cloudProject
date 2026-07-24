package com.enterprise.onboarding.domain.model;

import com.enterprise.onboarding.domain.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OnboardingTaskTest {

    private OnboardingTask pendingTask() {
        return OnboardingTask.builder()
                .id(UUID.randomUUID())
                .title("Allocate laptop")
                .owningDepartment(OwningDepartment.IT)
                .status(TaskStatus.PENDING)
                .build();
    }

    @Test
    void markInProgress_setsStatusAndUpdatedAt() {
        OnboardingTask task = pendingTask();

        task.markInProgress();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(task.getUpdatedAt()).isNotNull();
    }

    @Test
    void markCompleted_setsStatusAndCompletedAt() {
        OnboardingTask task = pendingTask();

        task.markCompleted();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task.getCompletedAt()).isNotNull();
    }

    @Test
    void markCompleted_whenAlreadyCompleted_throws() {
        OnboardingTask task = pendingTask();
        task.markCompleted();

        assertThatThrownBy(task::markCompleted)
                .isInstanceOf(InvalidStateTransitionException.class);
    }
}
