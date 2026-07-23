package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.Asset;
import com.enterprise.onboarding.infrastructure.persistence.entity.AssetEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssetEntityMapper {
    AssetEntity toEntity(Asset asset);
    Asset toDomain(AssetEntity entity);
}
