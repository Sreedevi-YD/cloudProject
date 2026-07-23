package com.enterprise.onboarding.domain.repository;

import com.enterprise.onboarding.domain.model.Employee;

import java.util.Optional;
import java.util.UUID;

/** Port implemented by the infrastructure layer (JPA adapter). */
public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(UUID id);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    PageResult<Employee> search(String query, PageRequest pageRequest);
    boolean existsByWorkEmail(String workEmail);
}
