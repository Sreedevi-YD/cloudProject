package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.OnboardingRequest;
import com.enterprise.onboarding.infrastructure.persistence.entity.OnboardingRequestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OnboardingRequestEntityMapper {
    OnboardingRequestEntity toEntity(OnboardingRequest request);
    OnboardingRequest toDomain(OnboardingRequestEntity entity);
}
