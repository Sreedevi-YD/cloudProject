package com.enterprise.onboarding.application.mapper;

import com.enterprise.onboarding.application.dto.document.DocumentDto;
import com.enterprise.onboarding.domain.model.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentDto toDto(Document document);
}
