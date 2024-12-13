package com.jarnevermant.projectservice.service;

import com.jarnevermant.projectservice.dto.EmployeeResponse;
import com.jarnevermant.projectservice.dto.ProjectRequest;
import com.jarnevermant.projectservice.dto.ProjectResponse;
import com.jarnevermant.projectservice.model.Project;
import com.jarnevermant.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WebClient webClient;

    public void createProject(ProjectRequest projectRequest) {
        System.out.println("Line 1");
        String employeeIdentifier = projectRequest.getProjectLeadEmployeeIdentifier();
        System.out.println("Line 2");
        EmployeeResponse employeeResponse = fetchEmployeeByEmployeeIdentifier(employeeIdentifier);
        System.out.println("Line 3");

        if (!employeeResponse.getRole().equals("Project Lead")) {
            throw new IllegalArgumentException("The employee does not have the role of Project Lead");
        }

        Project project = Project.builder()
                .projectNumber(UUID.randomUUID().toString())
                .name(projectRequest.getName())
                .description(projectRequest.getDescription())
                .startDate(projectRequest.getStartDate())
                .endDate(projectRequest.getEndDate())
                .projectLeadEmployeeIdentifier(projectRequest.getProjectLeadEmployeeIdentifier())
                .build();

        projectRepository.save(project);
    }

    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(this::mapToProjectResponse).toList();
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .projectNumber(project.getProjectNumber())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectLeadEmployeeIdentifier(project.getProjectLeadEmployeeIdentifier())
                .build();
    }

    private EmployeeResponse fetchEmployeeByEmployeeIdentifier(String employeeIdentifier) {
        return webClient.get()
                .uri("http://localhost:8080/api/employee",
                        uriBuilder -> uriBuilder.queryParam("employeeIdentifier", employeeIdentifier).build())
                .retrieve()
                .bodyToMono(EmployeeResponse[].class)
                .map(employeeResponses -> employeeResponses.length > 0 ? employeeResponses[0] : null)
                .block();
    }

}
