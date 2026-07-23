package com.enterprise.onboarding.application.service;

import com.enterprise.onboarding.application.annotation.Auditable;
import com.enterprise.onboarding.application.dto.employee.CreateEmployeeRequest;
import com.enterprise.onboarding.application.dto.employee.EmployeeDto;
import com.enterprise.onboarding.application.dto.employee.UpdateEmployeeRequest;
import com.enterprise.onboarding.application.mapper.EmployeeMapper;
import com.enterprise.onboarding.application.port.in.EmployeeService;
import com.enterprise.onboarding.domain.exception.DomainException;
import com.enterprise.onboarding.domain.exception.ResourceNotFoundException;
import com.enterprise.onboarding.domain.model.AuditAction;
import com.enterprise.onboarding.domain.model.Employee;
import com.enterprise.onboarding.domain.repository.EmployeeRepository;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Auditable(action = AuditAction.CREATED, entityType = "Employee")
    public EmployeeDto createEmployee(CreateEmployeeRequest request, UUID actingUserId) {
        if (employeeRepository.existsByWorkEmail(request.workEmail())) {
            throw new DomainException("An employee with work email " + request.workEmail() + " already exists");
        }
        Employee employee = employeeMapper.toDomain(request);
        employee.setId(UUID.randomUUID());
        employee.setEmployeeCode(generateEmployeeCode());
        employee.setActive(true);
        employee.setCreatedAt(Instant.now());
        employee.setUpdatedAt(employee.getCreatedAt());
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    @Auditable(action = AuditAction.UPDATED, entityType = "Employee")
    public EmployeeDto updateEmployee(UUID employeeId, UpdateEmployeeRequest request, UUID actingUserId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        employeeMapper.updateDomain(request, employee);
        employee.setUpdatedAt(Instant.now());
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto getEmployee(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<EmployeeDto> searchEmployees(String query, PageRequest pageRequest) {
        PageResult<Employee> result = employeeRepository.search(query, pageRequest);
        return new PageResult<>(
                result.content().stream().map(employeeMapper::toDto).toList(),
                result.totalElements(),
                result.page(),
                result.size()
        );
    }

    private String generateEmployeeCode() {
        return "EMP-" + System.currentTimeMillis();
    }
}
