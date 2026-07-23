package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.Document;
import com.enterprise.onboarding.infrastructure.persistence.entity.DocumentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentEntityMapper {
    DocumentEntity toEntity(Document document);
    Document toDomain(DocumentEntity entity);
}
