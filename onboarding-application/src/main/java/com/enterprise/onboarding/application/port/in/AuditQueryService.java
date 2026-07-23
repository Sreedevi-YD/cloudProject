package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.audit.AuditLogDto;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;

import java.util.List;
import java.util.UUID;

public interface AuditQueryService {
    List<AuditLogDto> findForEntity(String entityType, UUID entityId);
    PageResult<AuditLogDto> findAll(PageRequest pageRequest);
}
