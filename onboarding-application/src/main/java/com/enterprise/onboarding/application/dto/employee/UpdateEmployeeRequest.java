package com.enterprise.onboarding.application.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        @NotBlank String designation,
        @NotBlank String department,
        UUID managerId,
        @Email String personalEmail,
        boolean active
) {
}
