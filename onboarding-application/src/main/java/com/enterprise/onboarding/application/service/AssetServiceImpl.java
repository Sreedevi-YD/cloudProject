package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.annotation.Auditable;
import com.enterprise.onboarding.application.dto.asset.AssetDto;
import com.enterprise.onboarding.application.dto.asset.AssignAssetRequest;
import com.enterprise.onboarding.application.event.AssetAssignedEvent;
import com.enterprise.onboarding.application.mapper.AssetMapper;
import com.enterprise.onboarding.application.port.in.AssetService;
import com.enterprise.onboarding.domain.exception.ResourceNotFoundException;
import com.enterprise.onboarding.domain.model.Asset;
import com.enterprise.onboarding.domain.model.AssetStatus;
import com.enterprise.onboarding.domain.model.AuditAction;
import com.enterprise.onboarding.domain.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Auditable(action = AuditAction.ASSIGNED, entityType = "Asset")
    public AssetDto assignAsset(UUID onboardingRequestId, UUID employeeId, AssignAssetRequest request, UUID assignedByUserId) {
        Asset asset = Asset.builder()
                .id(UUID.randomUUID())
                .onboardingRequestId(onboardingRequestId)
                .employeeId(employeeId)
                .assetType(request.assetType())
                .status(AssetStatus.REQUESTED)
                .build();
        asset.markAssigned(assignedByUserId, request.assetTag());

        Asset saved = assetRepository.save(asset);
        eventPublisher.publishEvent(new AssetAssignedEvent(saved.getId(), onboardingRequestId, employeeId, saved.getAssetType()));
        return assetMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetDto> listForOnboardingRequest(UUID onboardingRequestId) {
        return assetRepository.findByOnboardingRequestId(onboardingRequestId).stream()
                .map(assetMapper::toDto)
                .toList();
    }

    @Override
    @Auditable(action = AuditAction.UPDATED, entityType = "Asset")
    public AssetDto returnAsset(UUID assetId, UUID actingUserId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        asset.markReturned();
        return assetMapper.toDto(assetRepository.save(asset));
    }
}
