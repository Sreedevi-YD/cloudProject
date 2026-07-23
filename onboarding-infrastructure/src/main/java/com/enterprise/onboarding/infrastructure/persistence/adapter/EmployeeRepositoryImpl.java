package com.enterprise.onboarding.infrastructure.persistence.adapter;

import com.enterprise.onboarding.domain.model.Employee;
import com.enterprise.onboarding.domain.repository.EmployeeRepository;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;
import com.enterprise.onboarding.infrastructure.persistence.entity.EmployeeEntity;
import com.enterprise.onboarding.infrastructure.persistence.mapper.EmployeeEntityMapper;
import com.enterprise.onboarding.infrastructure.persistence.repository.EmployeeJpaRepository;
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
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeEntityMapper mapper;

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity saved = jpaRepository.save(mapper.toEntity(employee));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Employee> findByEmployeeCode(String employeeCode) {
        return jpaRepository.findByEmployeeCode(employeeCode).map(mapper::toDomain);
    }

    @Override
    public PageResult<Employee> search(String query, PageRequest pageRequest) {
        Specification<EmployeeEntity> spec = buildSearchSpecification(query);
        Page<EmployeeEntity> page = jpaRepository.findAll(
                spec, org.springframework.data.domain.PageRequest.of(pageRequest.page(), pageRequest.size()));

        List<Employee> content = page.getContent().stream().map(mapper::toDomain).toList();
        return new PageResult<>(content, page.getTotalElements(), pageRequest.page(), pageRequest.size());
    }

    @Override
    public boolean existsByWorkEmail(String workEmail) {
        return jpaRepository.existsByWorkEmail(workEmail);
    }

    private Specification<EmployeeEntity> buildSearchSpecification(String query) {
        if (!StringUtils.hasText(query)) {
            return Specification.where(null);
        }
        String like = "%" + query.toLowerCase() + "%";
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("firstName")), like));
            predicates.add(cb.like(cb.lower(root.get("lastName")), like));
            predicates.add(cb.like(cb.lower(root.get("employeeCode")), like));
            predicates.add(cb.like(cb.lower(root.get("workEmail")), like));
            predicates.add(cb.like(cb.lower(root.get("department")), like));
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
