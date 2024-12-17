package com.jarnevermant.projectservice.service;

import com.jarnevermant.projectservice.dto.EmployeeResponse;
import com.jarnevermant.projectservice.dto.ProjectRequest;
import com.jarnevermant.projectservice.dto.ProjectResponse;
import com.jarnevermant.projectservice.exception.EmployeeNotFoundException;
import com.jarnevermant.projectservice.exception.InvalidEmployeeRoleException;
import com.jarnevermant.projectservice.exception.ProjectNotFoundException;
import com.jarnevermant.projectservice.model.Project;
import com.jarnevermant.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WebClient webClient;

    @Transactional
    public void createProject(ProjectRequest projectRequest) {
        String employeeIdentifier = projectRequest.getProjectLead();
        EmployeeResponse employeeResponse = fetchEmployeeByEmployeeIdentifier(employeeIdentifier);

        if (!employeeResponse.getRole().equals("Project Lead")) {
            throw new InvalidEmployeeRoleException("The employee does not have the role of Project Lead");
        }

        Project project = Project.builder()
                .projectIdentifier(UUID.randomUUID().toString())
                .name(projectRequest.getName())
                .description(projectRequest.getDescription())
                .startDate(projectRequest.getStartDate())
                .endDate(projectRequest.getEndDate())
                .projectLead(projectRequest.getProjectLead())
                .build();

        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(this::mapToProjectResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getActiveProjects() {
        LocalDate today = LocalDate.now();
        return projectRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today)
                .stream().map(this::mapToProjectResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(String projectIdentifier) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier)
                .orElseThrow(() -> new ProjectNotFoundException("The project does not exist"));
        return this.mapToProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> searchProjectsByProjectLead(String searchTerm) {
        List<EmployeeResponse> employees = this.fetchEmployeesByName(searchTerm);

        return projectRepository.findProjectsByProjectLeadIn(
                employees.stream().map(EmployeeResponse::getEmployeeIdentifier).toList()
        ).stream().map(this::mapToProjectResponse).toList();
    }

    @Transactional
    public ProjectResponse updateProject(String projectIdentifier, ProjectRequest projectRequest) {
        Project project = projectRepository.findByProjectIdentifier(projectIdentifier)
                .orElseThrow(() -> new ProjectNotFoundException("The project does not exist"));

        // Handle optional fields
        if (projectRequest.getName() != null) {
            project.setName(projectRequest.getName());
        }
        if (projectRequest.getDescription() != null) {
            project.setDescription(projectRequest.getDescription());
        }
        if (projectRequest.getStartDate() != null) {
            project.setStartDate(projectRequest.getStartDate());
        }
        if (projectRequest.getEndDate() != null) {
            project.setEndDate(projectRequest.getEndDate());
        }
        if (projectRequest.getProjectLead() != null) {
            EmployeeResponse employeeResponse = fetchEmployeeByEmployeeIdentifier(projectRequest.getProjectLead());

            if (!employeeResponse.getRole().equals("Project Lead")) {
                throw new InvalidEmployeeRoleException("The employee does not have the role of Project Lead");
            }

            project.setProjectLead(projectRequest.getProjectLead());

        }

        projectRepository.save(project);

        return this.mapToProjectResponse(project);
    }

    @Transactional
    public void deleteProject(String projectIdentifier) {
        if (!projectRepository.existsByProjectIdentifier(projectIdentifier)) {
            throw new ProjectNotFoundException("The project does not exist");
        }
        projectRepository.deleteByProjectIdentifier(projectIdentifier);
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        return ProjectResponse.builder()
                .projectIdentifier(project.getProjectIdentifier())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .projectLead(project.getProjectLead())
                .build();
    }

    private EmployeeResponse fetchEmployeeByEmployeeIdentifier(String employeeIdentifier) {
        return webClient.get()
                .uri("http://localhost:8080/api/employee",
                        uriBuilder -> uriBuilder.pathSegment(employeeIdentifier).build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new EmployeeNotFoundException("The employee does not exist"))
                )
                .bodyToMono(EmployeeResponse.class)
                .block();
    }

    private List<EmployeeResponse> fetchEmployeesByName(String searchTerm) {
        return webClient.get()
                .uri("http://localhost:8080/api/employee",
                        uriBuilder -> uriBuilder.queryParam("search", searchTerm).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<EmployeeResponse>>() {})
                .block();
    }

}
