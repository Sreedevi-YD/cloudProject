package com.enterprise.onboarding.presentation.controller;

import com.enterprise.onboarding.application.dto.common.PageResponse;
import com.enterprise.onboarding.application.dto.employee.CreateEmployeeRequest;
import com.enterprise.onboarding.application.dto.employee.EmployeeDto;
import com.enterprise.onboarding.application.dto.employee.UpdateEmployeeRequest;
import com.enterprise.onboarding.application.port.in.EmployeeService;
import com.enterprise.onboarding.domain.repository.PageRequest;
import com.enterprise.onboarding.presentation.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto created = employeeService.createEmployee(request, CurrentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDto> update(
            @PathVariable UUID employeeId, @Valid @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeId, request, CurrentUser.id()));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> get(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(employeeService.getEmployee(employeeId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeDto>> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = employeeService.searchEmployees(query, new PageRequest(page, size));
        return ResponseEntity.ok(PageResponse.from(result, dto -> dto));
    }
}
