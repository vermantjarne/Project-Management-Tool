package com.jarnevermant.employeeservice.service;

import com.jarnevermant.employeeservice.dto.EmployeeRequest;
import com.jarnevermant.employeeservice.dto.EmployeeResponse;
import com.jarnevermant.employeeservice.model.Employee;
import com.jarnevermant.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public void addEmployee(EmployeeRequest employeeRequest) {
        Employee employee = Employee.builder()
            .employeeIdentifier(UUID.randomUUID().toString())
            .firstName(employeeRequest.getFirstName())
            .lastName(employeeRequest.getLastName())
            .role(employeeRequest.getRole())
            .startDate(employeeRequest.getStartDate())
            .build();

        employeeRepository.save(employee);
    }

    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(this::mapToEmployeeResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByEmployeeIdentifiers(List<String> employeeIdentifier) {
        return employeeRepository.findByEmployeeIdentifierIn(employeeIdentifier).stream()
                .map(employee -> EmployeeResponse.builder()
                        .employeeIdentifier(employee.getEmployeeIdentifier())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .role(employee.getRole())
                        .startDate(employee.getStartDate())
                        .build()
                ).toList();
    }

    private EmployeeResponse mapToEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
            .employeeIdentifier(employee.getEmployeeIdentifier())
            .firstName(employee.getFirstName())
            .lastName(employee.getLastName())
            .role(employee.getRole())
            .startDate(employee.getStartDate())
            .build();
    }

}
