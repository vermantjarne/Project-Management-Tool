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
public class TaskResponse {
    private String taskIdentifier;
    private String name;
    private String description;
    private LocalDate deadline;
    private LocalDate dateCreated;
    private String status;
    private String project;
    private String employee;
}
