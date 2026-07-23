package com.enterprise.onboarding.application.event;

import java.util.UUID;

public record OnboardingRejectedEvent(UUID onboardingRequestId, UUID rejectedByUserId, String reason) {
}
