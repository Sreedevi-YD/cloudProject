package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.asset.AssetDto;
import com.enterprise.onboarding.application.dto.asset.AssignAssetRequest;

import java.util.List;
import java.util.UUID;

public interface AssetService {
    AssetDto assignAsset(UUID onboardingRequestId, UUID employeeId, AssignAssetRequest request, UUID assignedByUserId);
    List<AssetDto> listForOnboardingRequest(UUID onboardingRequestId);
    AssetDto returnAsset(UUID assetId, UUID actingUserId);
}
