package com.enterprise.onboarding.application.event;

import java.util.UUID;

public record OnboardingRequestCreatedEvent(UUID onboardingRequestId, UUID hiringManagerId) {
}
