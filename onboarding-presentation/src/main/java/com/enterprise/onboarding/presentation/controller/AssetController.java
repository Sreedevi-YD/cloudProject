package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.asset.AssetDto;
import com.enterprise.onboarding.application.dto.asset.AssignAssetRequest;
import com.enterprise.onboarding.application.port.in.AssetService;
import com.enterprise.onboarding.presentation.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Workflow step 4: IT allocates assets and accounts once the request is approved. */
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    public ResponseEntity<AssetDto> assign(
            @RequestParam UUID onboardingRequestId,
            @RequestParam UUID employeeId,
            @Valid @RequestBody AssignAssetRequest request) {
        AssetDto assigned = assetService.assignAsset(onboardingRequestId, employeeId, request, CurrentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(assigned);
    }

    @PostMapping("/{assetId}/return")
    public ResponseEntity<AssetDto> returnAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(assetService.returnAsset(assetId, CurrentUser.id()));
    }

    @GetMapping
    public ResponseEntity<List<AssetDto>> listForRequest(@RequestParam UUID onboardingRequestId) {
        return ResponseEntity.ok(assetService.listForOnboardingRequest(onboardingRequestId));
    }
}
