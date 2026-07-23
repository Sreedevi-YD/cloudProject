package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.AuditLog;
import com.enterprise.onboarding.infrastructure.persistence.entity.AuditLogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogEntityMapper {
    AuditLogEntity toEntity(AuditLog auditLog);
    AuditLog toDomain(AuditLogEntity entity);
}
