package com.jarnevermant.employeeservice.service;

import com.jarnevermant.employeeservice.dto.EmployeeRequest;
import com.jarnevermant.employeeservice.dto.EmployeeResponse;
import com.jarnevermant.employeeservice.exception.EmployeeNotFoundException;
import com.jarnevermant.employeeservice.model.Employee;
import com.jarnevermant.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(this::mapToEmployeeResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(String employeeIdentifier) {
        Employee employee = employeeRepository.findByEmployeeIdentifier(employeeIdentifier)
                .orElseThrow(() -> new EmployeeNotFoundException("The employee does not exist"));
        return this.mapToEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(String searchTerm) {
        List<Employee> employees = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
        return employees.stream().map(this::mapToEmployeeResponse).collect(Collectors.toList());
    }

    @Transactional
    public EmployeeResponse updateEmployee(String employeeIdentifier, EmployeeRequest employeeRequest) {
        Employee employee = employeeRepository.findByEmployeeIdentifier(employeeIdentifier)
                .orElseThrow(() -> new EmployeeNotFoundException("The employee does not exist"));

        // Handle optional fields
        if (employeeRequest.getFirstName() != null) {
            employee.setFirstName(employeeRequest.getFirstName());
        }
        if (employeeRequest.getLastName() != null) {
            employee.setLastName(employeeRequest.getLastName());
        }
        if (employeeRequest.getRole() != null) {

            employee.setRole(employeeRequest.getRole());
        }
        if (employeeRequest.getStartDate() != null) {
            employee.setStartDate(employeeRequest.getStartDate());
        }

        employeeRepository.save(employee);

        return this.mapToEmployeeResponse(employee);
    }

    @Transactional
    public void deleteEmployee(String employeeIdentifier) {
        if (!employeeRepository.existsByEmployeeIdentifier(employeeIdentifier)) {
            throw new EmployeeNotFoundException("The employee does not exist");
        }
        employeeRepository.deleteByEmployeeIdentifier(employeeIdentifier);
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
