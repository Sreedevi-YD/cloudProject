package com.enterprise.onboarding.application.mapper;

import com.enterprise.onboarding.application.dto.audit.AuditLogDto;
import com.enterprise.onboarding.domain.model.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogDto toDto(AuditLog auditLog);
}
