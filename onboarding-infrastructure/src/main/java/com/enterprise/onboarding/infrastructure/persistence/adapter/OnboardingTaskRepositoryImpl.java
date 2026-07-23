package com.enterprise.onboarding.infrastructure.persistence.adapter;

import com.enterprise.onboarding.domain.model.OnboardingTask;
import com.enterprise.onboarding.domain.model.OwningDepartment;
import com.enterprise.onboarding.domain.repository.OnboardingTaskRepository;
import com.enterprise.onboarding.infrastructure.persistence.mapper.OnboardingTaskEntityMapper;
import com.enterprise.onboarding.infrastructure.persistence.repository.OnboardingTaskJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OnboardingTaskRepositoryImpl implements OnboardingTaskRepository {

    private final OnboardingTaskJpaRepository jpaRepository;
    private final OnboardingTaskEntityMapper mapper;

    @Override
    public OnboardingTask save(OnboardingTask task) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(task)));
    }

    @Override
    public List<OnboardingTask> saveAll(List<OnboardingTask> tasks) {
        List<com.enterprise.onboarding.infrastructure.persistence.entity.OnboardingTaskEntity> entities =
                tasks.stream().map(mapper::toEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<OnboardingTask> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OnboardingTask> findByOnboardingRequestId(UUID onboardingRequestId) {
        return jpaRepository.findByOnboardingRequestId(onboardingRequestId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OnboardingTask> findByOwningDepartment(OwningDepartment department) {
        return jpaRepository.findByOwningDepartment(department).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OnboardingTask> findByAssignedToUserId(UUID userId) {
        return jpaRepository.findByAssignedToUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
