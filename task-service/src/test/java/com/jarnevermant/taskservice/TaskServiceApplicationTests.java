package com.jarnevermant.taskservice;

import com.jarnevermant.taskservice.dto.EmployeeResponse;
import com.jarnevermant.taskservice.dto.ProjectResponse;
import com.jarnevermant.taskservice.dto.TaskRequest;
import com.jarnevermant.taskservice.dto.TaskResponse;
import com.jarnevermant.taskservice.exception.EmployeeNotFoundException;
import com.jarnevermant.taskservice.exception.ProjectNotFoundException;
import com.jarnevermant.taskservice.exception.TaskNotFoundException;
import com.jarnevermant.taskservice.model.Task;
import com.jarnevermant.taskservice.repository.TaskRepository;
import com.jarnevermant.taskservice.service.TaskService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceApplicationTests {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

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
        ReflectionTestUtils.setField(taskService, "employeeServiceBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(taskService, "projectServiceBaseUrl", "http://localhost:8081");
    }

    @Test
    public void testCreateTask_Success() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Implement Notification System");
        taskRequest.setDescription("Develop a system to send automated notifications to users for appointment confirmations and reminders.");
        taskRequest.setDeadline(LocalDate.of(2025,2,28));
        taskRequest.setProject("PROJ-123");
        taskRequest.setEmployee("EMP-123");

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setProjectIdentifier("PROJ-123");

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeIdentifier("EMP-123");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProjectResponse.class)).thenReturn(Mono.just(projectResponse));
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(employeeResponse));

        // Act
        taskService.createTask(taskRequest);

        // Assert
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testCreateTask_EmployeeNotFound() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Implement Notification System");
        taskRequest.setDescription("Develop a system to send automated notifications to users for appointment confirmations and reminders.");
        taskRequest.setDeadline(LocalDate.of(2025,2,28));
        taskRequest.setProject("PROJ-123");
        taskRequest.setEmployee("EMP-123");

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setProjectIdentifier("PROJ-123");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProjectResponse.class)).thenReturn(Mono.just(projectResponse));
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.error(new EmployeeNotFoundException("The employee does not exist")));

        // Act & Assert
        EmployeeNotFoundException thrown = assertThrows(EmployeeNotFoundException.class, () -> {
            taskService.createTask(taskRequest);
        });

        assertEquals("The employee does not exist", thrown.getMessage());

        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    public void testCreateTask_ProjectNotFound() {
        // Arrange
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Implement Notification System");
        taskRequest.setDescription("Develop a system to send automated notifications to users for appointment confirmations and reminders.");
        taskRequest.setDeadline(LocalDate.of(2025,2,28));
        taskRequest.setProject("PROJ-123");
        taskRequest.setEmployee("EMP-123");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProjectResponse.class)).thenReturn(Mono.error(new ProjectNotFoundException("The project does not exist")));

        // Act & Assert
        ProjectNotFoundException thrown = assertThrows(ProjectNotFoundException.class, () -> {
            taskService.createTask(taskRequest);
        });

        assertEquals("The project does not exist", thrown.getMessage());

        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    public void testGetAllTasks() {
        // Arrange
        Task task = new Task();
        task.setTaskIdentifier("TASK-123");
        task.setName("Implement Notification System");
        task.setDescription("Develop a system to send automated notifications to users for appointment confirmations and reminders.");
        task.setDeadline(LocalDate.of(2025,2,28));
        task.setStatus("Not Started");
        task.setProject("PROJ-123");
        task.setEmployee("EMP-123");

        when(taskRepository.findAll()).thenReturn(List.of(task));

        // Act
        List<TaskResponse> tasks = taskService.getAllTasks();

        // Assert
        assertEquals(1, tasks.size());
        assertEquals("TASK-123", tasks.get(0).getTaskIdentifier());
        assertEquals("Implement Notification System", tasks.get(0).getName());
        assertEquals("Develop a system to send automated notifications to users for appointment confirmations and reminders.", tasks.get(0).getDescription());
        assertEquals(LocalDate.of(2025,2,28), tasks.get(0).getDeadline());
        assertEquals("Not Started", tasks.get(0).getStatus());
        assertEquals("PROJ-123", tasks.get(0).getProject());
        assertEquals("EMP-123", tasks.get(0).getEmployee());

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testGetActiveTasks() {
        // Arrange
        Task task = new Task();
        task.setTaskIdentifier("TASK-123");
        task.setName("Implement Notification System");
        task.setDescription("Develop a system to send automated notifications to users.");
        task.setDeadline(LocalDate.of(2025, 2, 28));
        task.setStatus("In Progress");
        task.setProject("PROJ-123");
        task.setEmployee("EMP-123");

        when(taskRepository.findByStatus("In Progress")).thenReturn(List.of(task));

        // Act
        List<TaskResponse> activeTasks = taskService.getActiveTasks();

        // Assert
        assertEquals(1, activeTasks.size());
        assertEquals("TASK-123", activeTasks.get(0).getTaskIdentifier());
        assertEquals("In Progress", activeTasks.get(0).getStatus());

        verify(taskRepository, times(1)).findByStatus("In Progress");
    }

    @Test
    public void testGetTask_Success() {
        // Arrange
        String taskIdentifier = "TASK-123";

        Task task = new Task();
        task.setTaskIdentifier(taskIdentifier);
        task.setName("Implement Notification System");
        task.setDescription("Develop a system to send automated notifications to users.");
        task.setDeadline(LocalDate.of(2025, 2, 28));
        task.setStatus("In Progress");
        task.setProject("PROJ-123");
        task.setEmployee("EMP-123");

        when(taskRepository.findByTaskIdentifier(taskIdentifier)).thenReturn(Optional.of(task));

        // Act
        TaskResponse taskResponse = taskService.getTask(taskIdentifier);

        // Assert
        assertNotNull(taskResponse);
        assertEquals(taskIdentifier, taskResponse.getTaskIdentifier());
        assertEquals("Implement Notification System", taskResponse.getName());

        verify(taskRepository, times(1)).findByTaskIdentifier(taskIdentifier);
    }

    @Test
    public void testGetTask_TaskNotFound() {
        // Arrange
        String taskIdentifier = "TASK-123";

        when(taskRepository.findByTaskIdentifier(taskIdentifier)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTask(taskIdentifier);
        });

        assertEquals("The task does not exist", thrown.getMessage());
        verify(taskRepository, times(1)).findByTaskIdentifier(taskIdentifier);
    }

    @Test
    public void testGetTasksByProject_Success() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        Task task = new Task();
        task.setTaskIdentifier("TASK-123");
        task.setName("Implement Notification System");
        task.setDescription("Develop a system to send automated notifications to users.");
        task.setDeadline(LocalDate.of(2025, 2, 28));
        task.setStatus("In Progress");
        task.setProject(projectIdentifier);
        task.setEmployee("EMP-123");

        when(taskRepository.findByProject(projectIdentifier)).thenReturn(List.of(task));

        // Act
        List<TaskResponse> tasksByProject = taskService.getTasksByProject(projectIdentifier);

        // Assert
        assertEquals(1, tasksByProject.size());
        assertEquals("TASK-123", tasksByProject.get(0).getTaskIdentifier());
        assertEquals(projectIdentifier, tasksByProject.get(0).getProject());

        verify(taskRepository, times(1)).findByProject(projectIdentifier);
    }

    @Test
    public void testGetTasksByProject_NotFound() {
        // Arrange
        String projectIdentifier = "PROJ-123";

        when(taskRepository.findByProject(projectIdentifier)).thenReturn(List.of());

        // Act
        List<TaskResponse> tasksByProject = taskService.getTasksByProject(projectIdentifier);

        // Assert
        assertTrue(tasksByProject.isEmpty());

        verify(taskRepository, times(1)).findByProject(projectIdentifier);
    }

    @Test
    public void testUpdateTask_Success() {
        // Arrange
        String taskIdentifier = "TASK-123";

        Task task = new Task();
        task.setTaskIdentifier(taskIdentifier);
        task.setName("Implement Notification System");
        task.setDescription("Develop a system to send automated notifications to users for appointment confirmations and reminders.");
        task.setDeadline(LocalDate.of(2025,2,28));
        task.setStatus("Not Started");
        task.setProject("PROJ-123");

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus("In Progress");

        when(taskRepository.findByTaskIdentifier(taskIdentifier)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskResponse updatedTask = taskService.updateTask(taskIdentifier, taskRequest);

        // Assert
        assertNotNull(updatedTask);
        assertEquals("In Progress", updatedTask.getStatus());

        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testUpdateTask_TaskNotFound() {
        // Arrange
        String taskIdentifier = "TASK-123";

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus("In Progress");

        when(taskRepository.findByTaskIdentifier(taskIdentifier)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(taskIdentifier, taskRequest);
        });

        // Assert
        assertEquals("The task does not exist", thrown.getMessage());

        verify(taskRepository, times(0)).save(any(Task.class));
    }

    @Test
    public void testDeleteTask_Success() {
        // Arrange
        String taskIdentifier = "TASK-123";

        when(taskRepository.existsByTaskIdentifier(taskIdentifier)).thenReturn(true);

        // Act
        taskService.deleteTask(taskIdentifier);

        // Assert
        verify(taskRepository, times(1)).deleteByTaskIdentifier(taskIdentifier);
    }

    @Test
    public void testDeleteTask_TaskNotFound() {
        // Arrange
        String taskIdentifier = "TASK-123";

        when(taskRepository.existsByTaskIdentifier(taskIdentifier)).thenReturn(false);

        // Act & Assert
        TaskNotFoundException thrown = assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(taskIdentifier);
        });

        assertEquals("The task does not exist", thrown.getMessage());

        verify(taskRepository, times(0)).deleteByTaskIdentifier(anyString());
    }

}
