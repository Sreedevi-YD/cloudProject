package com.enterprise.onboarding.application.event;

import com.enterprise.onboarding.domain.model.DocumentType;

import java.util.UUID;

public record DocumentUploadedEvent(UUID documentId, UUID onboardingRequestId, DocumentType documentType) {
}
