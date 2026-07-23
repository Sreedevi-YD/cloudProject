package com.enterprise.onboarding.application.dto.onboarding;

import jakarta.validation.constraints.NotBlank;

public record RejectOnboardingRequest(@NotBlank String reason) {
}
