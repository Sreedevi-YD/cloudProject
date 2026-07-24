package com.enterprise.onboarding.infrastructure.persistence.entity;

import com.enterprise.onboarding.domain.model.AssetStatus;
import com.enterprise.onboarding.domain.model.AssetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "onboarding_request_id")
    private UUID onboardingRequestId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false, length = 30)
    private AssetType assetType;

    @Column(name = "asset_tag", length = 100)
    private String assetTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AssetStatus status;

    @Column(name = "assigned_by_user_id")
    private UUID assignedByUserId;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "returned_at")
    private Instant returnedAt;
}
