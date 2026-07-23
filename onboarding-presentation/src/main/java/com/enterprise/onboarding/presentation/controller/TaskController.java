package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.task.TaskDto;
import com.enterprise.onboarding.application.dto.task.UpdateTaskStatusRequest;
import com.enterprise.onboarding.application.port.in.TaskService;
import com.enterprise.onboarding.domain.model.OwningDepartment;
import com.enterprise.onboarding.presentation.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Workflow steps 4-5: department dashboards and task completion feed step 6 (auto-complete). */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> listForRequest(@RequestParam UUID onboardingRequestId) {
        return ResponseEntity.ok(taskService.listForOnboardingRequest(onboardingRequestId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<List<TaskDto>> dashboard(
            @RequestParam(required = false) OwningDepartment department,
            @RequestParam(required = false) UUID assignedToUserId) {
        return ResponseEntity.ok(taskService.listDashboard(department, assignedToUserId));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateStatus(
            @PathVariable UUID taskId, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, request, CurrentUser.id()));
    }
}
