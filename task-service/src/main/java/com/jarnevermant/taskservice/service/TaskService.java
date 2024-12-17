package com.jarnevermant.taskservice.service;

import com.jarnevermant.taskservice.dto.EmployeeResponse;
import com.jarnevermant.taskservice.dto.ProjectResponse;
import com.jarnevermant.taskservice.dto.TaskRequest;
import com.jarnevermant.taskservice.dto.TaskResponse;
import com.jarnevermant.taskservice.exception.EmployeeNotFoundException;
import com.jarnevermant.taskservice.exception.ProjectNotFoundException;
import com.jarnevermant.taskservice.exception.TaskNotFoundException;
import com.jarnevermant.taskservice.model.Task;
import com.jarnevermant.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final WebClient webClient;

    @Transactional
    public void createTask(TaskRequest taskRequest) {
        String projectIdentifier = taskRequest.getProject();
        fetchProjectByProjectIdentifier(projectIdentifier);

        Task task = Task.builder()
                .taskIdentifier(UUID.randomUUID().toString())
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .dateCreated(LocalDate.now())
                .deadline(taskRequest.getDeadline())
                .status("Not Started")
                .project(projectIdentifier)
                .build();

        String employeeIdentifier = taskRequest.getEmployee();
        if (employeeIdentifier != null) {
            fetchEmployeeByEmployeeIdentifier(employeeIdentifier);
            task.setEmployee(employeeIdentifier);
        }

        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(this::mapToTaskResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getActiveTasks() {
        return taskRepository.findByStatus("In Progress")
                .stream().map(this::mapToTaskResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskBy(String taskIdentifier) {
        Task task = taskRepository.findByTaskIdentifier(taskIdentifier)
                .orElseThrow(() -> new TaskNotFoundException("The task does not exist"));
        return this.mapToTaskResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(String projectIdentifier) {
        return taskRepository.findByProject(projectIdentifier)
                .stream().map(this::mapToTaskResponse).toList();
    }

    @Transactional
    public TaskResponse updateTask(String taskIdentifier, TaskRequest taskRequest) {
        Task task = taskRepository.findByTaskIdentifier(taskIdentifier)
                .orElseThrow(() -> new TaskNotFoundException("The task does not exist"));

        // Handle optional fields
        if (taskRequest.getName() != null) {
            task.setName(taskRequest.getName());
        }
        if (taskRequest.getDescription() != null) {
            task.setDescription(taskRequest.getDescription());
        }
        if (taskRequest.getDeadline() != null) {
            task.setDeadline(taskRequest.getDeadline());
        }
        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }
        if (taskRequest.getProject() != null) {
            fetchProjectByProjectIdentifier(taskRequest.getProject());
            task.setProject(taskRequest.getProject());
        }
        if (taskRequest.getEmployee() != null) {
            fetchEmployeeByEmployeeIdentifier(taskRequest.getProject());
            task.setEmployee(taskRequest.getEmployee());
        }

        taskRepository.save(task);

        return this.mapToTaskResponse(task);
    }

    @Transactional
    public void deleteTask(String taskIdentifier) {
        if (!taskRepository.existsByTaskIdentifier(taskIdentifier)) {
            throw new TaskNotFoundException("The task does not exist");
        }
        taskRepository.deleteByTaskIdentifier(taskIdentifier);
    }

    private TaskResponse mapToTaskResponse(Task task) {
        return TaskResponse.builder()
                .taskIdentifier(task.getTaskIdentifier())
                .name(task.getName())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .dateCreated(task.getDateCreated())
                .status(task.getStatus())
                .project(task.getProject())
                .employee(task.getEmployee())
                .build();
    }

    private ProjectResponse fetchProjectByProjectIdentifier(String projectIdentifier) {
        return webClient.get()
                .uri("http://localhost:8081/api/project",
                        uriBuilder -> uriBuilder.pathSegment(projectIdentifier).build())
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new ProjectNotFoundException("The project does not exist"))
                )
                .bodyToMono(ProjectResponse.class)
                .block();
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

}
