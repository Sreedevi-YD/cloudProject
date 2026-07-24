package com.enterprise.onboarding.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.local")
public record LocalStorageProperties(String basePath) {
}
