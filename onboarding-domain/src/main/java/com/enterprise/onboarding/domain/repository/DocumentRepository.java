package com.enterprise.onboarding.domain.repository;

import com.enterprise.onboarding.domain.model.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Document save(Document document);
    Optional<Document> findById(UUID id);
    List<Document> findByOnboardingRequestId(UUID onboardingRequestId);
    List<Document> findByEmployeeId(UUID employeeId);
}
