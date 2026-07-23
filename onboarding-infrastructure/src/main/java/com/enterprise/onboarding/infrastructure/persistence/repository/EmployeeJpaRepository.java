package com.enterprise.onboarding.infrastructure.persistence.repository;

import com.enterprise.onboarding.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, UUID>, JpaSpecificationExecutor<EmployeeEntity> {
    Optional<EmployeeEntity> findByEmployeeCode(String employeeCode);
    boolean existsByWorkEmail(String workEmail);
}
