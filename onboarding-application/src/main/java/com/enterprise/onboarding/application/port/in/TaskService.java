package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.task.TaskDto;
import com.enterprise.onboarding.application.dto.task.UpdateTaskStatusRequest;
import com.enterprise.onboarding.domain.model.OwningDepartment;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskDto> listForOnboardingRequest(UUID onboardingRequestId);
    List<TaskDto> listDashboard(OwningDepartment department, UUID assignedToUserId);
    TaskDto updateStatus(UUID taskId, UpdateTaskStatusRequest request, UUID actingUserId);
}
