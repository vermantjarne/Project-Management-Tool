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
public class ProjectResponse {
    private String projectIdentifier;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectLead;
}
