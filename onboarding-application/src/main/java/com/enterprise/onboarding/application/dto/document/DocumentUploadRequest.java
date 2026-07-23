package com.enterprise.onboarding.application.dto.document;

import com.enterprise.onboarding.domain.model.DocumentType;

import java.io.InputStream;
import java.util.UUID;

/** Framework-agnostic stand-in for a multipart file; the presentation layer maps MultipartFile into this. */
public record DocumentUploadRequest(
        UUID onboardingRequestId,
        UUID employeeId,
        DocumentType documentType,
        String fileName,
        String contentType,
        long fileSizeBytes,
        InputStream content
) {
}
