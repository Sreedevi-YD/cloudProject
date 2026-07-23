package com.enterprise.onboarding.domain.repository;

import com.enterprise.onboarding.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);
    PageResult<AuditLog> findAll(PageRequest pageRequest);
}
