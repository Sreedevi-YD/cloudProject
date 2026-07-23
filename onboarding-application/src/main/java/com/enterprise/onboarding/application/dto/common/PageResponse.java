package com.enterprise.onboarding.application.dto.common;

import com.enterprise.onboarding.domain.repository.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, long totalElements, int totalPages, int page, int size) {
    public static <S, T> PageResponse<T> from(PageResult<S> result, Function<S, T> mapper) {
        return new PageResponse<>(
                result.content().stream().map(mapper).toList(),
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}
