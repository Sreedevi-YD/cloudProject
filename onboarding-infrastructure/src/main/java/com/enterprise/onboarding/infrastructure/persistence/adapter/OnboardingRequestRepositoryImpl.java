package com.enterprise.onboarding.infrastructure.persistence.adapter;

import com.enterprise.onboarding.domain.model.OnboardingRequest;
import com.enterprise.onboarding.domain.model.OnboardingStatus;
import com.enterprise.onboarding.domain.repository.OnboardingRequestRepository;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;
import com.enterprise.onboarding.infrastructure.persistence.entity.OnboardingRequestEntity;
import com.enterprise.onboarding.infrastructure.persistence.mapper.OnboardingRequestEntityMapper;
import com.enterprise.onboarding.infrastructure.persistence.repository.OnboardingRequestJpaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OnboardingRequestRepositoryImpl implements OnboardingRequestRepository {

    private final OnboardingRequestJpaRepository jpaRepository;
    private final OnboardingRequestEntityMapper mapper;

    @Override
    public OnboardingRequest save(OnboardingRequest request) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(request)));
    }

    @Override
    public Optional<OnboardingRequest> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OnboardingRequest> findByHiringManagerId(UUID managerId) {
        return jpaRepository.findByHiringManagerId(managerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OnboardingRequest> findByStatus(OnboardingStatus status) {
        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public PageResult<OnboardingRequest> search(String query, OnboardingStatus status, PageRequest pageRequest) {
        Specification<OnboardingRequestEntity> spec = buildSpecification(query, status);
        Page<OnboardingRequestEntity> page = jpaRepository.findAll(
                spec, org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size()));

        List<OnboardingRequest> content = page.getContent().stream().map(mapper::toDomain).toList();
        return new PageResult<>(content, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }

    private Specification<OnboardingRequestEntity> buildSpecification(String query, OnboardingStatus status) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(query)) {
                String like = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("candidateName")), like),
                        cb.like(cb.lower(root.get("candidateEmail")), like),
                        cb.like(cb.lower(root.get("department")), like)
                ));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
