package com.enterprise.onboarding.infrastructure.audit;

import com.enterprise.onboarding.application.annotation.Auditable;
import com.enterprise.onboarding.domain.model.AuditLog;
import com.enterprise.onboarding.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Writes an {@link AuditLog} row for every use-case method annotated {@link Auditable}, once it
 * completes successfully. Entity id is read off the returned DTO's {@code id()}/{@code getId()}
 * accessor; the acting user id is the first UUID method parameter whose name matches a known
 * "actor" convention (enabled by the {@code -parameters} compiler flag).
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private static final Set<String> ACTOR_PARAM_NAMES = Set.of(
            "actingUserId", "hrUserId", "managerUserId", "approverId", "uploadedByUserId",
            "assignedByUserId", "requestedByUserId", "byUserId");

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            recordAuditLog(joinPoint, auditable, result);
        } catch (Exception e) {
            log.warn("Failed to write audit log for {}: {}", joinPoint.getSignature(), e.getMessage());
        }

        return result;
    }

    private void recordAuditLog(ProceedingJoinPoint joinPoint, Auditable auditable, Object result) {
        UUID entityId = extractEntityId(result);
        UUID actingUserId = extractActingUserId(joinPoint);

        AuditLog auditLog = AuditLog.builder()
                .id(UUID.randomUUID())
                .entityType(auditable.entityType())
                .entityId(entityId)
                .action(auditable.action())
                .performedByUserId(actingUserId)
                .details(joinPoint.getSignature().toShortString())
                .occurredAt(Instant.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    private UUID extractEntityId(Object result) {
        if (result == null) {
            return null;
        }
        for (String accessor : List.of("id", "getId")) {
            try {
                Method method = result.getClass().getMethod(accessor);
                Object value = method.invoke(result);
                if (value instanceof UUID uuid) {
                    return uuid;
                }
            } catch (ReflectiveOperationException ignored) {
                // try the next accessor convention
            }
        }
        return null;
    }

    private UUID extractActingUserId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames == null) {
            return null;
        }
        for (int i = 0; i < paramNames.length; i++) {
            if (ACTOR_PARAM_NAMES.contains(paramNames[i]) && args[i] instanceof UUID uuid) {
                return uuid;
            }
        }
        return null;
    }
}
