package com.enterprise.onboarding.infrastructure.persistence.adapter;

import com.enterprise.onboarding.domain.model.Asset;
import com.enterprise.onboarding.domain.repository.AssetRepository;
import com.enterprise.onboarding.infrastructure.persistence.mapper.AssetEntityMapper;
import com.enterprise.onboarding.infrastructure.persistence.repository.AssetJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetRepository {

    private final AssetJpaRepository jpaRepository;
    private final AssetEntityMapper mapper;

    @Override
    public Asset save(Asset asset) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(asset)));
    }

    @Override
    public Optional<Asset> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Asset> findByOnboardingRequestId(UUID onboardingRequestId) {
        return jpaRepository.findByOnboardingRequestId(onboardingRequestId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Asset> findByEmployeeId(UUID employeeId) {
        return jpaRepository.findByEmployeeId(employeeId).stream().map(mapper::toDomain).toList();
    }
}
