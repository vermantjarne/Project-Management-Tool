package com.jarnevermant.employeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private String employeeIdentifier;
    private String firstName;
    private String lastName;
    private String role;
    private LocalDate startDate;
}