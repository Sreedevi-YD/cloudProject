package com.enterprise.onboarding.domain.repository;

/** Framework-agnostic paging request so the domain layer has no Spring Data dependency. */
public record PageRequest(int page, int size) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
    }

    public int offset() {
        return page * size;
    }
}
