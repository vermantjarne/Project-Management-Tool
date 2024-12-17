package com.jarnevermant.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String name;
    private String description;
    private LocalDate deadline;
    private String status;
    private String project;
    private String employee;
}
