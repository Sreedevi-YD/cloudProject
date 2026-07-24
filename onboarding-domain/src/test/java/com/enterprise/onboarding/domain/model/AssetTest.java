package com.enterprise.onboarding.domain.model;

import com.enterprise.onboarding.domain.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetTest {

    private Asset requestedAsset() {
        return Asset.builder()
                .id(UUID.randomUUID())
                .assetType(AssetType.LAPTOP)
                .status(AssetStatus.REQUESTED)
                .build();
    }

    @Test
    void markAssigned_fromRequested_setsTagAndTimestamp() {
        Asset asset = requestedAsset();

        asset.markAssigned(UUID.randomUUID(), "LAP-001");

        assertThat(asset.getStatus()).isEqualTo(AssetStatus.ASSIGNED);
        assertThat(asset.getAssetTag()).isEqualTo("LAP-001");
        assertThat(asset.getAssignedAt()).isNotNull();
    }

    @Test
    void markAssigned_whenNotRequested_throws() {
        Asset asset = requestedAsset();
        asset.markAssigned(UUID.randomUUID(), "LAP-001");

        assertThatThrownBy(() -> asset.markAssigned(UUID.randomUUID(), "LAP-002"))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void markReturned_fromAssigned_succeeds() {
        Asset asset = requestedAsset();
        asset.markAssigned(UUID.randomUUID(), "LAP-001");

        asset.markReturned();

        assertThat(asset.getStatus()).isEqualTo(AssetStatus.RETURNED);
        assertThat(asset.getReturnedAt()).isNotNull();
    }

    @Test
    void markReturned_whenNotAssigned_throws() {
        Asset asset = requestedAsset();

        assertThatThrownBy(asset::markReturned)
                .isInstanceOf(InvalidStateTransitionException.class);
    }
}
