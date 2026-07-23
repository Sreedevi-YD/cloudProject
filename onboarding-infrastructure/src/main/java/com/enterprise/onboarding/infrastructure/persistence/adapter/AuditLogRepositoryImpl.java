package com.enterprise.onboarding.infrastructure.persistence.adapter;

import com.enterprise.onboarding.domain.model.AuditLog;
import com.enterprise.onboarding.domain.repository.AuditLogRepository;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;
import com.enterprise.onboarding.infrastructure.persistence.entity.AuditLogEntity;
import com.enterprise.onboarding.infrastructure.persistence.mapper.AuditLogEntityMapper;
import com.enterprise.onboarding.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;
    private final AuditLogEntityMapper mapper;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(auditLog)));
    }

    @Override
    public List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId) {
        return jpaRepository.findByEntityTypeAndEntityId(entityType, entityId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<AuditLog> findAll(PageRequest pageRequest) {
        Page<AuditLogEntity> page = jpaRepository.findAllByOrderByOccurredAtDesc(
                org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size()));
        List<AuditLog> content = page.getContent().stream().map(mapper::toDomain).toList();
        return new PageResult<>(content, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }
}
