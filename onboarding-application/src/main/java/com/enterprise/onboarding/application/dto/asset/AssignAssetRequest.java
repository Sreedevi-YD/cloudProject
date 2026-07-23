package com.enterprise.onboarding.application.dto.asset;

import com.enterprise.onboarding.domain.model.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssignAssetRequest(
        @NotNull AssetType assetType,
        @NotBlank String assetTag
) {
}
