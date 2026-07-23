package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.annotation.Auditable;
import com.enterprise.onboarding.application.dto.task.TaskDto;
import com.enterprise.onboarding.application.dto.task.UpdateTaskStatusRequest;
import com.enterprise.onboarding.application.mapper.TaskMapper;
import com.enterprise.onboarding.application.port.in.OnboardingWorkflowService;
import com.enterprise.onboarding.application.port.in.TaskService;
import com.enterprise.onboarding.domain.exception.ResourceNotFoundException;
import com.enterprise.onboarding.domain.model.AuditAction;
import com.enterprise.onboarding.domain.model.OnboardingTask;
import com.enterprise.onboarding.domain.model.OwningDepartment;
import com.enterprise.onboarding.domain.model.TaskStatus;
import com.enterprise.onboarding.domain.repository.OnboardingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final TaskMapper taskMapper;
    private final OnboardingWorkflowService onboardingWorkflowService;

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> listForOnboardingRequest(UUID onboardingRequestId) {
        return onboardingTaskRepository.findByOnboardingRequestId(onboardingRequestId).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> listDashboard(OwningDepartment department, UUID assignedToUserId) {
        List<OnboardingTask> tasks = assignedToUserId != null
                ? onboardingTaskRepository.findByAssignedToUserId(assignedToUserId)
                : onboardingTaskRepository.findByOwningDepartment(department);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    @Override
    @Auditable(action = AuditAction.UPDATED, entityType = "OnboardingTask")
    public TaskDto updateStatus(UUID taskId, UpdateTaskStatusRequest request, UUID actingUserId) {
        OnboardingTask task = onboardingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("OnboardingTask", taskId));

        if (request.status() == TaskStatus.COMPLETED) {
            task.markCompleted();
        } else if (request.status() == TaskStatus.IN_PROGRESS) {
            task.markInProgress();
        } else {
            task.setStatus(request.status());
        }

        OnboardingTask saved = onboardingTaskRepository.save(task);

        if (saved.getStatus() == TaskStatus.COMPLETED) {
            onboardingWorkflowService.completeIfAllTasksDone(saved.getOnboardingRequestId());
        }

        return taskMapper.toDto(saved);
    }
}
