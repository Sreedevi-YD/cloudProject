package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.audit.AuditLogDto;
import com.enterprise.onboarding.application.dto.common.PageResponse;
import com.enterprise.onboarding.application.port.in.AuditQueryService;
import com.enterprise.onboarding.domain.repository.PageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditQueryService auditQueryService;

    @GetMapping
    public ResponseEntity<PageResponse<AuditLogDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = auditQueryService.findAll(new PageRequest(page, size));
        return ResponseEntity.ok(PageResponse.from(result, dto -> dto));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogDto>> findForEntity(
            @PathVariable String entityType, @PathVariable UUID entityId) {
        return ResponseEntity.ok(auditQueryService.findForEntity(entityType, entityId));
    }
}
