package com.enterprise.onboarding.application.port.in;

import com.enterprise.onboarding.application.dto.employee.CreateEmployeeRequest;
import com.enterprise.onboarding.application.dto.employee.EmployeeDto;
import com.enterprise.onboarding.application.dto.employee.UpdateEmployeeRequest;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.domain.repository.PageResult;

import java.util.UUID;

public interface EmployeeService {
    EmployeeDto createEmployee(CreateEmployeeRequest request, UUID actingUserId);
    EmployeeDto updateEmployee(UUID employeeId, UpdateEmployeeRequest request, UUID actingUserId);
    EmployeeDto getEmployee(UUID employeeId);
    PageResult<EmployeeDto> searchEmployees(String query, PageRequest pageRequest);
}
