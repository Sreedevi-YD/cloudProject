package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.document.DocumentDownload;
import com.enterprise.onboarding.application.dto.document.DocumentDto;
import com.enterprise.onboarding.application.dto.document.DocumentUploadRequest;
import com.enterprise.onboarding.application.port.in.DocumentService;
import com.enterprise.onboarding.domain.model.DocumentType;
import com.enterprise.onboarding.presentation.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentDto> upload(
            @RequestParam UUID onboardingRequestId,
            @RequestParam UUID employeeId,
            @RequestParam DocumentType documentType,
            @RequestPart MultipartFile file) {

        try {
            DocumentUploadRequest request = new DocumentUploadRequest(
                    onboardingRequestId, employeeId, documentType,
                    file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());

            DocumentDto uploaded = documentService.upload(request, CurrentUser.id());
            return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file", e);
        }
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID documentId) {
        DocumentDownload download = documentService.download(documentId, CurrentUser.id());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        download.contentType() != null ? download.contentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(download.fileName()).build().toString())
                .body(new InputStreamResource(download.content()));
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> listForRequest(@RequestParam UUID onboardingRequestId) {
        return ResponseEntity.ok(documentService.listForOnboardingRequest(onboardingRequestId));
    }
}
