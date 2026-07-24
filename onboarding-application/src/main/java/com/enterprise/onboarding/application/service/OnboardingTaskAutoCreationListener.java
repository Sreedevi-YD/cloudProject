package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.event.OnboardingApprovedEvent;
import com.enterprise.onboarding.domain.model.OnboardingTask;
import com.enterprise.onboarding.domain.model.OwningDepartment;
import com.enterprise.onboarding.domain.model.TaskStatus;
import com.enterprise.onboarding.domain.repository.OnboardingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Functional requirement 6: tasks are auto-created the moment a request is approved,
 * fanned out to the departments that must act (IT provisioning, HR orientation).
 * Runs AFTER_COMMIT so tasks are never created for an approval that later rolls back.
 *
 * <p>REQUIRES_NEW is essential here, not decorative: at AFTER_COMMIT time the original
 * transaction's JPA resources are still bound to the thread (Spring unbinds them only after
 * firing this callback). Without REQUIRES_NEW, saveAll() silently joins that already-committed
 * transaction instead of opening a fresh one — the inserts get buffered into a persistence
 * context nothing will ever flush, so no rows are written and no exception is thrown either.
 */
@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class OnboardingTaskAutoCreationListener {

    private final OnboardingTaskRepository onboardingTaskRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onApproved(OnboardingApprovedEvent event) {
        Instant now = Instant.now();
        UUID requestId = event.onboardingRequestId();

        List<OnboardingTask> defaultTasks = List.of(
                newTask(requestId, "Allocate laptop", "Assign a laptop asset to the new hire.", OwningDepartment.IT, now),
                newTask(requestId, "Create email account", "Provision the corporate email account.", OwningDepartment.IT, now),
                newTask(requestId, "Grant VPN access", "Provision VPN credentials.", OwningDepartment.IT, now),
                newTask(requestId, "Schedule orientation", "Book Day-1 orientation session.", OwningDepartment.HR, now),
                newTask(requestId, "Collect onboarding documents", "Verify uploaded documents are complete.", OwningDepartment.HR, now)
        );

        onboardingTaskRepository.saveAll(defaultTasks);
    }

    private OnboardingTask newTask(UUID requestId, String title, String description, OwningDepartment dept, Instant now) {
        return OnboardingTask.builder()
                .id(UUID.randomUUID())
                .onboardingRequestId(requestId)
                .title(title)
                .description(description)
                .owningDepartment(dept)
                .status(TaskStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
