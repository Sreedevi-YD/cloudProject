package com.enterprise.onboarding.infrastructure.persistence.mapper;

import com.enterprise.onboarding.domain.model.Employee;
import com.enterprise.onboarding.infrastructure.persistence.entity.EmployeeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeEntityMapper {
    EmployeeEntity toEntity(Employee employee);
    Employee toDomain(EmployeeEntity entity);
}
