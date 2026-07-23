package com.enterprise.onboarding.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

    @Id
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 40)
    private String employeeCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "personal_email", length = 255)
    private String personalEmail;

    @Column(name = "work_email", nullable = false, unique = true, length = 255)
    private String workEmail;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "designation", length = 150)
    private String designation;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "manager_id", columnDefinition = "uniqueidentifier")
    private UUID managerId;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
