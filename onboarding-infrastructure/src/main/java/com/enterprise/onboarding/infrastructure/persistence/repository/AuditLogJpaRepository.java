package com.enterprise.onboarding.infrastructure.persistence.repository;

import com.enterprise.onboarding.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, UUID> {
    List<AuditLogEntity> findByEntityTypeAndEntityId(String entityType, UUID entityId);
    Page<AuditLogEntity> findAllByOrderByOccurredAtDesc(Pageable pageable);
}
