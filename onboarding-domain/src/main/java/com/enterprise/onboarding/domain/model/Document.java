package com.enterprise.onboarding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/** Metadata only — the binary content lives in MinIO; {@code storageKey} points to the object. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    private UUID id;
    private UUID onboardingRequestId;
    private UUID employeeId;
    private DocumentType documentType;
    private String fileName;
    private String contentType;
    private long fileSizeBytes;
    private String storageBucket;
    private String storageKey;
    private String checksum;
    private UUID uploadedByUserId;
    private Instant uploadedAt;
}
