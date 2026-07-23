package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.dto.audit.AuditLogDto;
import com.enterprise.onboarding.application.mapper.AuditLogMapper;
import com.enterprise.onboarding.application.port.in.AuditQueryService;
import com.enterprise.onboarding.domain.repository.AuditLogRepository;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditQueryServiceImpl implements AuditQueryService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public List<AuditLogDto> findForEntity(String entityType, UUID entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(auditLogMapper::toDto)
                .toList();
    }

    @Override
    public PageResult<AuditLogDto> findAll(PageRequest pageRequest) {
        PageResult<com.enterprise.onboarding.domain.model.AuditLog> result = auditLogRepository.findAll(pageRequest);
        return new PageResult<>(
                result.content().stream().map(auditLogMapper::toDto).toList(),
                result.totalElements(),
                result.page(),
                result.size()
        );
    }
}
