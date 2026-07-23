package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.annotation.Auditable;
import com.enterprise.onboarding.application.dto.onboarding.CreateOnboardingRequest;
import com.enterprise.onboarding.application.dto.onboarding.OnboardingRequestDto;
import com.enterprise.onboarding.application.event.OnboardingApprovedEvent;
import com.enterprise.onboarding.application.event.OnboardingCompletedEvent;
import com.enterprise.onboarding.application.event.OnboardingRejectedEvent;
import com.enterprise.onboarding.application.event.OnboardingRequestCreatedEvent;
import com.enterprise.onboarding.application.mapper.OnboardingRequestMapper;
import com.enterprise.onboarding.application.port.in.OnboardingWorkflowService;
import com.enterprise.onboarding.domain.exception.ResourceNotFoundException;
import com.enterprise.onboarding.domain.model.AuditAction;
import com.enterprise.onboarding.domain.model.OnboardingRequest;
import com.enterprise.onboarding.domain.model.OnboardingStatus;
import com.enterprise.onboarding.domain.model.OnboardingTask;
import com.enterprise.onboarding.domain.model.TaskStatus;
import com.enterprise.onboarding.domain.repository.OnboardingRequestRepository;
import com.enterprise.onboarding.domain.repository.OnboardingTaskRepository;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingWorkflowServiceImpl implements OnboardingWorkflowService {

    private final OnboardingRequestRepository onboardingRequestRepository;
    private final OnboardingTaskRepository onboardingTaskRepository;
    private final OnboardingRequestMapper onboardingRequestMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Auditable(action = AuditAction.CREATED, entityType = "OnboardingRequest")
    public OnboardingRequestDto createRequest(CreateOnboardingRequest request, UUID hrUserId) {
        OnboardingRequest onboardingRequest = onboardingRequestMapper.toDomain(request);
        onboardingRequest.setId(UUID.randomUUID());
        onboardingRequest.setCreatedByUserId(hrUserId);
        onboardingRequest.setStatus(OnboardingStatus.PENDING_APPROVAL);
        onboardingRequest.setCreatedAt(Instant.now());
        onboardingRequest.setUpdatedAt(onboardingRequest.getCreatedAt());

        OnboardingRequest saved = onboardingRequestRepository.save(onboardingRequest);
        eventPublisher.publishEvent(new OnboardingRequestCreatedEvent(saved.getId(), saved.getHiringManagerId()));
        return onboardingRequestMapper.toDto(saved);
    }

    @Override
    @Auditable(action = AuditAction.APPROVED, entityType = "OnboardingRequest")
    public OnboardingRequestDto approveRequest(UUID requestId, UUID managerUserId) {
        OnboardingRequest request = findOrThrow(requestId);
        request.approve(managerUserId);
        request.moveToDocumentsPending();
        OnboardingRequest saved = onboardingRequestRepository.save(request);

        eventPublisher.publishEvent(new OnboardingApprovedEvent(saved.getId(), managerUserId));
        return onboardingRequestMapper.toDto(saved);
    }

    @Override
    @Auditable(action = AuditAction.REJECTED, entityType = "OnboardingRequest")
    public OnboardingRequestDto rejectRequest(UUID requestId, UUID managerUserId, String reason) {
        OnboardingRequest request = findOrThrow(requestId);
        request.reject(managerUserId, reason);
        OnboardingRequest saved = onboardingRequestRepository.save(request);

        eventPublisher.publishEvent(new OnboardingRejectedEvent(saved.getId(), managerUserId, reason));
        return onboardingRequestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OnboardingRequestDto getRequest(UUID requestId) {
        return onboardingRequestMapper.toDto(findOrThrow(requestId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OnboardingRequestDto> search(String query, OnboardingStatus status, PageRequest pageRequest) {
        PageResult<OnboardingRequest> result = onboardingRequestRepository.search(query, status, pageRequest);
        return new PageResult<>(
                result.content().stream().map(onboardingRequestMapper::toDto).toList(),
                result.totalElements(),
                result.page(),
                result.size()
        );
    }

    @Override
    public void completeIfAllTasksDone(UUID requestId) {
        List<OnboardingTask> tasks = onboardingTaskRepository.findByOnboardingRequestId(requestId);
        boolean allDone = !tasks.isEmpty() && tasks.stream().allMatch(t -> t.getStatus() == TaskStatus.COMPLETED);
        if (!allDone) {
            return;
        }
        OnboardingRequest request = findOrThrow(requestId);
        if (request.getStatus() == OnboardingStatus.COMPLETED) {
            return;
        }
        request.moveToTasksInProgress();
        request.complete();
        onboardingRequestRepository.save(request);
        eventPublisher.publishEvent(new OnboardingCompletedEvent(requestId, request.getEmployeeId()));
    }

    private OnboardingRequest findOrThrow(UUID requestId) {
        return onboardingRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("OnboardingRequest", requestId));
    }
}
