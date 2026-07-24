package com.enterprise.onboarding.infrastructure.storage;

import com.enterprise.onboarding.application.port.out.FileStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * {@link FileStoragePort} implementation for the disk/Docker-free "local" profile — stores
 * documents under a base directory on the local filesystem instead of MinIO. Same contract as
 * {@link MinioFileStorageAdapter}, so DocumentService and every caller are unaffected by which
 * one is active.
 */
@Component
@Profile("local")
@RequiredArgsConstructor
@EnableConfigurationProperties(LocalStorageProperties.class)
public class LocalFileSystemStorageAdapter implements FileStoragePort {

    private final LocalStorageProperties properties;

    @Override
    public StoredObject store(String bucket, String key, String contentType, long sizeBytes, InputStream content) {
        Path target = resolve(bucket, key);
        try {
            Files.createDirectories(target.getParent());
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
            return new StoredObject(bucket, key, null);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store file at " + target, e);
        }
    }

    @Override
    public InputStream retrieve(String bucket, String key) {
        try {
            return Files.newInputStream(resolve(bucket, key));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file for bucket=" + bucket + " key=" + key, e);
        }
    }

    @Override
    public void delete(String bucket, String key) {
        try {
            Files.deleteIfExists(resolve(bucket, key));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete file for bucket=" + bucket + " key=" + key, e);
        }
    }

    private Path resolve(String bucket, String key) {
        Path base = Path.of(properties.basePath()).toAbsolutePath().normalize();
        Path resolved = base.resolve(bucket).resolve(key).normalize();
        if (!resolved.startsWith(base)) {
            throw new IllegalArgumentException("Resolved path escapes the storage base directory: " + key);
        }
        return resolved;
    }
}
