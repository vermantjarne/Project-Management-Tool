package com.jarnevermant.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectLeadEmployeeIdentifier;
}
