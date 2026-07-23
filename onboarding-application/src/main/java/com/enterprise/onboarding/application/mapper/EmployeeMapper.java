package com.enterprise.onboarding.application.mapper;

import com.enterprise.onboarding.application.dto.employee.CreateEmployeeRequest;
import com.enterprise.onboarding.application.dto.employee.EmployeeDto;
import com.enterprise.onboarding.application.dto.employee.UpdateEmployeeRequest;
import com.enterprise.onboarding.domain.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDto toDto(Employee employee);

    Employee toDomain(CreateEmployeeRequest request);

    void updateDomain(UpdateEmployeeRequest request, @MappingTarget Employee employee);
}
