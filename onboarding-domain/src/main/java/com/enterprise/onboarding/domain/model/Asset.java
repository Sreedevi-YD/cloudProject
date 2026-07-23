package com.enterprise.onboarding.domain.model;

import com.enterprise.onboarding.domain.exception.InvalidStateTransitionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    private UUID id;
    private UUID onboardingRequestId;
    private UUID employeeId;
    private AssetType assetType;
    private String assetTag;
    private AssetStatus status;
    private UUID assignedByUserId;
    private Instant assignedAt;
    private Instant returnedAt;

    public void markAssigned(UUID byUserId, String assetTag) {
        if (this.status != AssetStatus.REQUESTED) {
            throw new InvalidStateTransitionException("Asset " + id + " is not in REQUESTED state");
        }
        this.status = AssetStatus.ASSIGNED;
        this.assetTag = assetTag;
        this.assignedByUserId = byUserId;
        this.assignedAt = Instant.now();
    }

    public void markReturned() {
        if (this.status != AssetStatus.ASSIGNED) {
            throw new InvalidStateTransitionException("Asset " + id + " is not in ASSIGNED state");
        }
        this.status = AssetStatus.RETURNED;
        this.returnedAt = Instant.now();
    }
}
