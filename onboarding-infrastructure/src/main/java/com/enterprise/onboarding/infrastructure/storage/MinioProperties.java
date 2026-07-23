package com.enterprise.onboarding.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.minio")
public record MinioProperties(String endpoint, String accessKey, String secretKey, boolean autoCreateBuckets) {
}
