package com.jarnevermant.employeeservice.controller;

import com.jarnevermant.employeeservice.dto.EmployeeRequest;
import com.jarnevermant.employeeservice.dto.EmployeeResponse;
import com.jarnevermant.employeeservice.dto.UpdateRoleRequest;
import com.jarnevermant.employeeservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void addEmployee(@RequestBody EmployeeRequest employeeRequest) {
        employeeService.addEmployee(employeeRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{employeeIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse getEmployeeByEmployeeIdentifier(@PathVariable String employeeIdentifier) {
        return employeeService.getEmployeeByEmployeeIdentifier(employeeIdentifier);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeResponse> searchEmployees(@RequestParam String search) {
        return employeeService.searchEmployees(search);
    }

    @PutMapping("/{employeeIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponse updateEmployee(@PathVariable String employeeIdentifier, @RequestBody UpdateRoleRequest updateRoleRequest) {
        return employeeService.updateEmployeeRole(employeeIdentifier, updateRoleRequest.getRole());
    }

    @DeleteMapping("/{employeeIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteEmployee(@PathVariable String employeeIdentifier) {
        employeeService.deleteEmployee(employeeIdentifier);
    }

}

