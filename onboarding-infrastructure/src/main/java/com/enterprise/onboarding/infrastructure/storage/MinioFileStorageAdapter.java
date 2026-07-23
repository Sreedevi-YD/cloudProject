package com.enterprise.onboarding.infrastructure.storage;

import com.enterprise.onboarding.application.port.out.FileStoragePort;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Implements {@link FileStoragePort} against MinIO. On AWS this is swapped for an S3 adapter
 * that satisfies the same port — no changes required in the application layer.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MinioFileStorageAdapter implements FileStoragePort {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public StoredObject store(String bucket, String key, String contentType, long sizeBytes, InputStream content) {
        try {
            ensureBucketExists(bucket);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .contentType(contentType)
                    .stream(content, sizeBytes, -1)
                    .build());
            return new StoredObject(bucket, key, null);
        } catch (Exception e) {
            log.error("Failed to store object {} in bucket {}", key, bucket, e);
            throw new StorageException("Failed to store file in MinIO", e);
        }
    }

    @Override
    public InputStream retrieve(String bucket, String key) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (Exception e) {
            log.error("Failed to retrieve object {} from bucket {}", key, bucket, e);
            throw new StorageException("Failed to retrieve file from MinIO", e);
        }
    }

    @Override
    public void delete(String bucket, String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (Exception e) {
            log.error("Failed to delete object {} from bucket {}", key, bucket, e);
            throw new StorageException("Failed to delete file from MinIO", e);
        }
    }

    private void ensureBucketExists(String bucket) throws Exception {
        if (!minioProperties.autoCreateBuckets()) {
            return;
        }
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
