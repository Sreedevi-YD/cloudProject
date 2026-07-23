package com.enterprise.onboarding.application.dto.employee;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeDto(
        UUID id,
        String employeeCode,
        String firstName,
        String lastName,
        String personalEmail,
        String workEmail,
        String phoneNumber,
        String designation,
        String department,
        UUID managerId,
        LocalDate dateOfJoining,
        boolean active
) {
}
