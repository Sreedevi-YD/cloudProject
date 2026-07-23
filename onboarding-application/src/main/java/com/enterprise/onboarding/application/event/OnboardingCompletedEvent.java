package com.enterprise.onboarding.application.event;

import java.util.UUID;

/** Business requirement step 6: fired once every task for the request is COMPLETED. */
public record OnboardingCompletedEvent(UUID onboardingRequestId, UUID employeeId) {
}
