package com.enterprise.onboarding.application.event;

import java.util.UUID;

/** Triggers auto-creation of IT/HR tasks (business requirement: step 4 follows approval). */
public record OnboardingApprovedEvent(UUID onboardingRequestId, UUID approvedByUserId) {
}
