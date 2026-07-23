package com.enterprise.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private UUID id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String personalEmail;
    private String workEmail;
    private String phoneNumber;
    private String designation;
    private String department;
    private UUID managerId;
    private LocalDate dateOfJoining;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public String fullName() {
        return firstName + " " + lastName;
    }
}
