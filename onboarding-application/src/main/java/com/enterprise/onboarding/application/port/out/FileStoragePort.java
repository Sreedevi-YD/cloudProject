package com.enterprise.onboarding.application.port.out;

import java.io.InputStream;

/** Implemented by the infrastructure layer against MinIO today; swappable for S3 on AWS migration. */
public interface FileStoragePort {

    StoredObject store(String bucket, String key, String contentType, long sizeBytes, InputStream content);

    InputStream retrieve(String bucket, String key);

    void delete(String bucket, String key);

    record StoredObject(String bucket, String key, String checksum) {
    }
}
