package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.annotation.Auditable;
import com.enterprise.onboarding.application.dto.document.DocumentDownload;
import com.enterprise.onboarding.application.dto.document.DocumentDto;
import com.enterprise.onboarding.application.dto.document.DocumentUploadRequest;
import com.enterprise.onboarding.application.event.DocumentUploadedEvent;
import com.enterprise.onboarding.application.mapper.DocumentMapper;
import com.enterprise.onboarding.application.port.in.DocumentService;
import com.enterprise.onboarding.application.port.out.FileStoragePort;
import com.enterprise.onboarding.domain.exception.ResourceNotFoundException;
import com.enterprise.onboarding.domain.model.AuditAction;
import com.enterprise.onboarding.domain.model.Document;
import com.enterprise.onboarding.domain.repository.DocumentRepository;
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
public class DocumentServiceImpl implements DocumentService {

    private static final String BUCKET = "onboarding-documents";

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final FileStoragePort fileStoragePort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Auditable(action = AuditAction.UPLOADED, entityType = "Document")
    public DocumentDto upload(DocumentUploadRequest request, UUID uploadedByUserId) {
        String key = "%s/%s/%s-%s".formatted(
                request.onboardingRequestId(), request.documentType(), UUID.randomUUID(), request.fileName());

        FileStoragePort.StoredObject stored = fileStoragePort.store(
                BUCKET, key, request.contentType(), request.fileSizeBytes(), request.content());

        Document document = Document.builder()
                .id(UUID.randomUUID())
                .onboardingRequestId(request.onboardingRequestId())
                .employeeId(request.employeeId())
                .documentType(request.documentType())
                .fileName(request.fileName())
                .contentType(request.contentType())
                .fileSizeBytes(request.fileSizeBytes())
                .storageBucket(stored.bucket())
                .storageKey(stored.key())
                .checksum(stored.checksum())
                .uploadedByUserId(uploadedByUserId)
                .uploadedAt(Instant.now())
                .build();

        Document saved = documentRepository.save(document);
        eventPublisher.publishEvent(new DocumentUploadedEvent(saved.getId(), saved.getOnboardingRequestId(), saved.getDocumentType()));
        return documentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Auditable(action = AuditAction.DOWNLOADED, entityType = "Document")
    public DocumentDownload download(UUID documentId, UUID requestedByUserId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", documentId));
        return new DocumentDownload(
                document.getFileName(),
                document.getContentType(),
                fileStoragePort.retrieve(document.getStorageBucket(), document.getStorageKey())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentDto> listForOnboardingRequest(UUID onboardingRequestId) {
        return documentRepository.findByOnboardingRequestId(onboardingRequestId).stream()
                .map(documentMapper::toDto)
                .toList();
    }
}
