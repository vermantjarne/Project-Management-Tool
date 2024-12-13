package com.jarnevermant.employeeservice.service;

import com.jarnevermant.employeeservice.dto.EmployeeRequest;
import com.jarnevermant.employeeservice.dto.EmployeeResponse;
import com.jarnevermant.employeeservice.model.Employee;
import com.jarnevermant.employeeservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public void addEmployee(EmployeeRequest employeeRequest){
        Employee employee = Employee.builder()
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

    private EmployeeResponse mapToEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
            .id(employee.getId())
            .firstName(employee.getFirstName())
            .lastName(employee.getLastName())
            .role(employee.getRole())
            .startDate(employee.getStartDate())
            .build();
    }

}
