package com.enterprise.onboarding.application.dto.onboarding;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/** Raised by HR to kick off the workflow (step 1). */
public record CreateOnboardingRequest(
        @NotBlank String candidateName,
        @NotBlank @Email String candidateEmail,
        @NotBlank String designation,
        @NotBlank String department,
        @NotNull LocalDate proposedJoiningDate,
        @NotNull UUID hiringManagerId
) {
}
