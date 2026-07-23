package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.document.DocumentDownload;
import com.enterprise.onboarding.application.dto.document.DocumentDto;
import com.enterprise.onboarding.application.dto.document.DocumentUploadRequest;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentDto upload(DocumentUploadRequest request, UUID uploadedByUserId);
    DocumentDownload download(UUID documentId, UUID requestedByUserId);
    List<DocumentDto> listForOnboardingRequest(UUID onboardingRequestId);
}
