package com.jarnevermant.projectservice;

import com.jarnevermant.projectservice.dto.EmployeeResponse;
import com.jarnevermant.projectservice.dto.ProjectRequest;
import com.jarnevermant.projectservice.dto.ProjectResponse;
import com.jarnevermant.projectservice.exception.InvalidEmployeeRoleException;
import com.jarnevermant.projectservice.exception.ProjectNotFoundException;
import com.jarnevermant.projectservice.model.Project;
import com.jarnevermant.projectservice.repository.ProjectRepository;
import com.jarnevermant.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceApplicationTests {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(projectService, "employeeServiceBaseUrl", "http://localhost:8080");
    }

    @Test
    public void testCreateProject_Success() {
        // Arrange
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Website");
        projectRequest.setDescription("A complete overhaul of our company's website, with the aim to modernize its design and user experience");
        projectRequest.setStartDate(LocalDate.of(2025,1,1));
        projectRequest.setEndDate(LocalDate.of(2025,6,30));
        projectRequest.setProjectLead("EMP-123");

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeIdentifier("EMP-123");
        employeeResponse.setRole("Project Lead");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(employeeResponse));

        // Act
        projectService.createProject(projectRequest);

        // Assert
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testCreateProject_InvalidEmployeeRole() {
        // Arrange
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Website");
        projectRequest.setDescription("A complete overhaul of our company's website, with the aim to modernize its design and user experience");
        projectRequest.setStartDate(LocalDate.of(2025,1,1));
        projectRequest.setEndDate(LocalDate.of(2025,6,30));
        projectRequest.setProjectLead("EMP-123");

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeIdentifier("EMP-123");
        employeeResponse.setRole("Developer");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(employeeResponse));

        // Act & Assert
        InvalidEmployeeRoleException thrown = assertThrows(InvalidEmployeeRoleException.class, () -> {
            projectService.createProject(projectRequest);
        });

        assertEquals("The employee does not have the role of Project Lead", thrown.getMessage());

        verify(projectRepository, times(0)).save(any(Project.class));
    }

    @Test
    public void testGetAllProjects() {
        // Arrange
        Project project = new Project();
        project.setProjectIdentifier("PROJ-123");
        project.setName("Website");
        project.setDescription("A complete overhaul of our company's website, with the aim to modernize its design and user experience");
        project.setStartDate(LocalDate.of(2025,1,1));
        project.setEndDate(LocalDate.of(2025,6,30));
        project.setProjectLead("EMP-123");

        when(projectRepository.findAll()).thenReturn(List.of(project));

        // Act
        List<ProjectResponse> projects = projectService.getAllProjects();

        // Assert
        assertEquals(1, projects.size());
        assertEquals("PROJ-123", projects.get(0).getProjectIdentifier());
        assertEquals("Website", projects.get(0).getName());
        assertEquals("A complete overhaul of our company's website, with the aim to modernize its design and user experience", projects.get(0).getDescription());
        assertEquals(LocalDate.of(2025,1,1), projects.get(0).getStartDate());
        assertEquals(LocalDate.of(2025,6,30), projects.get(0).getEndDate());
        assertEquals("EMP-123", projects.get(0).getProjectLead());

        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetActiveProjects_Success() {
        // Arrange
        LocalDate today = LocalDate.now();

        Project project = new Project();
        project.setProjectIdentifier("PROJ-123");
        project.setName("Website");
        project.setDescription("A complete overhaul of our company's website.");
        project.setStartDate(today.minusDays(10));
        project.setEndDate(today.plusDays(10));
        project.setProjectLead("EMP-123");

        when(projectRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today))
                .thenReturn(List.of(project));

        // Act
        List<ProjectResponse> activeProjects = projectService.getActiveProjects();

        // Assert
        assertEquals(1, activeProjects.size());
        assertEquals("PROJ-123", activeProjects.get(0).getProjectIdentifier());
        assertEquals("Website", activeProjects.get(0).getName());

        verify(projectRepository, times(1))
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
    }

    @Test
    public void testGetActiveProjects_NoProjects() {
        // Arrange
        LocalDate today = LocalDate.now();

        when(projectRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today))
                .thenReturn(List.of());

        // Act
        List<ProjectResponse> activeProjects = projectService.getActiveProjects();

        // Assert
        assertTrue(activeProjects.isEmpty());
        verify(projectRepository, times(1))
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
    }

    @Test
    public void testGetProject_Success() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        Project project = new Project();
        project.setProjectIdentifier(projectIdentifier);
        project.setName("Website");
        project.setDescription("A complete overhaul of our company's website.");
        project.setStartDate(LocalDate.of(2025, 1, 1));
        project.setEndDate(LocalDate.of(2025, 6, 30));
        project.setProjectLead("EMP-123");

        when(projectRepository.findByProjectIdentifier(projectIdentifier)).thenReturn(Optional.of(project));

        // Act
        ProjectResponse projectResponse = projectService.getProject(projectIdentifier);

        // Assert
        assertEquals("PROJ-123", projectResponse.getProjectIdentifier());
        assertEquals("Website", projectResponse.getName());

        verify(projectRepository, times(1)).findByProjectIdentifier(projectIdentifier);
    }

    @Test
    public void testGetProject_ProjectNotFound() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        when(projectRepository.findByProjectIdentifier(projectIdentifier)).thenReturn(Optional.empty());

        // Act & Assert
        ProjectNotFoundException thrown = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProject(projectIdentifier);
        });

        assertEquals("The project does not exist", thrown.getMessage());

        verify(projectRepository, times(1)).findByProjectIdentifier(projectIdentifier);
    }

    @Test
    public void testSearchProjectsByProjectLead_Success() {
        // Arrange
        String searchTerm = "pow";

        EmployeeResponse employee1 = new EmployeeResponse();
        employee1.setEmployeeIdentifier("EMP-123");
        employee1.setFirstName("Bertha");
        employee1.setLastName("Powells");

        EmployeeResponse employee2 = new EmployeeResponse();
        employee2.setEmployeeIdentifier("EMP-456");
        employee2.setFirstName("Nicholas");
        employee2.setLastName("Thompson");

        when(projectService.fetchEmployeesByName(searchTerm)).thenReturn(List.of(employee1, employee2));

        Project project1 = new Project();
        project1.setProjectIdentifier("PROJ-123");
        project1.setName("Website");
        project1.setDescription("A complete overhaul of our company's website.");
        project1.setProjectLead("EMP-123");

        Project project2 = new Project();
        project2.setProjectIdentifier("PROJ-456");
        project2.setName("Project Management Tool Frontend");
        project2.setDescription("A frontend application for our company's project management tool.");
        project2.setProjectLead("EMP-456");

        when(projectRepository.findProjectsByProjectLeadIn(List.of("EMP-123", "EMP-456")))
                .thenReturn(List.of(project1, project2));

        // Act
        List<ProjectResponse> projects = projectService.searchProjectsByProjectLead(searchTerm);

        // Assert
        assertEquals(2, projects.size());
        assertEquals("PROJ-123", projects.get(0).getProjectIdentifier());
        assertEquals("PROJ-456", projects.get(1).getProjectIdentifier());

        verify(projectRepository, times(1)).findProjectsByProjectLeadIn(List.of("EMP-123", "EMP-456"));
    }

    @Test
    public void testSearchProjectsByProjectLead_NotFound() {
        // Arrange
        String searchTerm = "pow";

        when(projectService.fetchEmployeesByName(searchTerm)).thenReturn(List.of());

        when(projectRepository.findProjectsByProjectLeadIn(List.of())).thenReturn(List.of());

        // Act
        List<ProjectResponse> projects = projectService.searchProjectsByProjectLead(searchTerm);

        // Assert
        assertTrue(projects.isEmpty());
        verify(projectRepository, times(1)).findProjectsByProjectLeadIn(List.of());
    }

    @Test
    public void testUpdateProject_Success() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        Project project = new Project();
        project.setProjectIdentifier(projectIdentifier);
        project.setName("Website");
        project.setDescription("A complete overhaul of our company's website, with the aim to modernize its design and user experience");
        project.setStartDate(LocalDate.of(2025,1,1));
        project.setEndDate(LocalDate.of(2025,6,30));
        project.setProjectLead("EMP-123");

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Website Redesign");

        when(projectRepository.findByProjectIdentifier(projectIdentifier)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // Act
        ProjectResponse updatedProject = projectService.updateProject(projectIdentifier, projectRequest);

        // Assert
        assertNotNull(updatedProject);
        assertEquals("Website Redesign", updatedProject.getName());

        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testUpdateProject_ProjectNotFound() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Website Redesign");

        when(projectRepository.findByProjectIdentifier(projectIdentifier)).thenReturn(Optional.empty());

        // Act & Assert
        ProjectNotFoundException thrown = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.updateProject(projectIdentifier, projectRequest);
        });

        // Assert
        assertEquals("The project does not exist", thrown.getMessage());

        verify(projectRepository, times(0)).save(any(Project.class));
    }

    @Test
    public void testDeleteProject_Success() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        when(projectRepository.existsByProjectIdentifier(projectIdentifier)).thenReturn(true);

        // Act
        projectService.deleteProject(projectIdentifier);

        // Assert
        verify(projectRepository, times(1)).deleteByProjectIdentifier(projectIdentifier);
    }

    @Test
    public void testDeleteProject_ProjectNotFound() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        when(projectRepository.existsByProjectIdentifier(projectIdentifier)).thenReturn(false);

        // Act & Assert
        ProjectNotFoundException thrown = assertThrows(ProjectNotFoundException.class, () -> {
            projectService.deleteProject(projectIdentifier);
        });

        assertEquals("The project does not exist", thrown.getMessage());

        verify(projectRepository, times(0)).deleteByProjectIdentifier(anyString());
    }

}
