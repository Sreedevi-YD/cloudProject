package com.enterprise.onboarding.application.mapper;

import com.enterprise.onboarding.application.dto.task.TaskDto;
import com.enterprise.onboarding.domain.model.OnboardingTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(OnboardingTask task);
}
