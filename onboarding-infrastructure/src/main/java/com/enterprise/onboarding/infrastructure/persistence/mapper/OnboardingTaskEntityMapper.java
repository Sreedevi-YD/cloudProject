package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.OnboardingTask;
import com.enterprise.onboarding.infrastructure.persistence.entity.OnboardingTaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OnboardingTaskEntityMapper {
    OnboardingTaskEntity toEntity(OnboardingTask task);
    OnboardingTask toDomain(OnboardingTaskEntity entity);
}
