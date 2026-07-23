package com.enterprise.onboarding.application.mapper;

import com.enterprise.onboarding.application.dto.asset.AssetDto;
import com.enterprise.onboarding.domain.model.Asset;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    AssetDto toDto(Asset asset);
}
