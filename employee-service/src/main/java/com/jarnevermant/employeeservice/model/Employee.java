package com.jarnevermant.employeeservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(value = "employee")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private Date startDate;
}
