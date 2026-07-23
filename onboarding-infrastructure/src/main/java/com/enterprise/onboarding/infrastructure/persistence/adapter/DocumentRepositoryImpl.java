package com.enterprise.onboarding.infrastructure.persistence.adapter;

import com.enterprise.onboarding.domain.model.Document;
import com.enterprise.onboarding.domain.repository.DocumentRepository;
import com.enterprise.onboarding.infrastructure.persistence.mapper.DocumentEntityMapper;
import com.enterprise.onboarding.infrastructure.persistence.repository.DocumentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryImpl implements DocumentRepository {

    private final DocumentJpaRepository jpaRepository;
    private final DocumentEntityMapper mapper;

    @Override
    public Document save(Document document) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(document)));
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Document> findByOnboardingRequestId(UUID onboardingRequestId) {
        return jpaRepository.findByOnboardingRequestId(onboardingRequestId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Document> findByEmployeeId(UUID employeeId) {
        return jpaRepository.findByEmployeeId(employeeId).stream().map(mapper::toDomain).toList();
    }
}
