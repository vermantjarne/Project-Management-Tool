package com.jarnevermant.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private String employeeIdentifier;
    private String firstName;
    private String lastName;
    private String role;
    private Date startDate;
}