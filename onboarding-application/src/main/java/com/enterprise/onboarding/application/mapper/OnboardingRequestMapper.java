package com.enterprise.onboarding.application.mapper;

import com.enterprise.onboarding.application.dto.onboarding.CreateOnboardingRequest;
import com.enterprise.onboarding.application.dto.onboarding.OnboardingRequestDto;
import com.enterprise.onboarding.domain.model.OnboardingRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OnboardingRequestMapper {

    OnboardingRequestDto toDto(OnboardingRequest request);

    OnboardingRequest toDomain(CreateOnboardingRequest request);
}
