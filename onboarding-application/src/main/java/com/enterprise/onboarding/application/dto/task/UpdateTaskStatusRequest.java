package com.enterprise.onboarding.application.dto.task;

import com.enterprise.onboarding.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(@NotNull TaskStatus status) {
}
