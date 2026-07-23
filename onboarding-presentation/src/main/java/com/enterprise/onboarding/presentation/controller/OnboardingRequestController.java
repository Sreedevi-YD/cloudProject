package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.common.PageResponse;
import com.enterprise.onboarding.application.dto.onboarding.CreateOnboardingRequest;
import com.enterprise.onboarding.application.dto.onboarding.OnboardingRequestDto;
import com.enterprise.onboarding.application.dto.onboarding.RejectOnboardingRequest;
import com.enterprise.onboarding.application.port.in.OnboardingWorkflowService;
import com.enterprise.onboarding.domain.model.OnboardingStatus;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.presentation.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Steps 1-2 of the onboarding workflow: HR creates the request, the manager approves/rejects it. */
@RestController
@RequestMapping("/api/v1/onboarding-requests")
@RequiredArgsConstructor
public class OnboardingRequestController {

    private final OnboardingWorkflowService onboardingWorkflowService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<OnboardingRequestDto> create(@Valid @RequestBody CreateOnboardingRequest request) {
        OnboardingRequestDto created = onboardingWorkflowService.createRequest(request, CurrentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{requestId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OnboardingRequestDto> approve(@PathVariable UUID requestId) {
        return ResponseEntity.ok(onboardingWorkflowService.approveRequest(requestId, CurrentUser.id()));
    }

    @PostMapping("/{requestId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OnboardingRequestDto> reject(
            @PathVariable UUID requestId, @Valid @RequestBody RejectOnboardingRequest request) {
        return ResponseEntity.ok(onboardingWorkflowService.rejectRequest(requestId, CurrentUser.id(), request.reason()));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<OnboardingRequestDto> get(@PathVariable UUID requestId) {
        return ResponseEntity.ok(onboardingWorkflowService.getRequest(requestId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<OnboardingRequestDto>> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) OnboardingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = onboardingWorkflowService.search(query, status, new PageRequest(page, size));
        return ResponseEntity.ok(PageResponse.from(result, dto -> dto));
    }
}
