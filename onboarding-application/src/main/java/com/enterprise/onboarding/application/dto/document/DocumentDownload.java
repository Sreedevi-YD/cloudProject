package com.enterprise.onboarding.application.dto.document;

import java.io.InputStream;

public record DocumentDownload(String fileName, String contentType, InputStream content) {
}
