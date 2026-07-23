package com.enterprise.onboarding.application.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String personalEmail,
        @NotBlank @Email String workEmail,
        String phoneNumber,
        @NotBlank String designation,
        @NotBlank String department,
        UUID managerId,
        @NotNull LocalDate dateOfJoining
) {
}
