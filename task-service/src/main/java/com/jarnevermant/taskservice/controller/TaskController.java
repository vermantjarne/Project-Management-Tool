package com.jarnevermant.taskservice.controller;

import com.jarnevermant.taskservice.dto.TaskRequest;
import com.jarnevermant.taskservice.dto.TaskResponse;
import com.jarnevermant.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createTask(@RequestBody TaskRequest taskRequest) {
        taskService.createTask(taskRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getActiveTasks() {
        return taskService.getActiveTasks();
    }

    @GetMapping("/{taskIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse getTask(@PathVariable String taskIdentifier) {
        return taskService.getTaskBy(taskIdentifier);
    }

    @GetMapping("/project/{projectIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskResponse> getTasksByProject(@PathVariable String projectIdentifier) {
        return taskService.getTasksByProject(projectIdentifier);
    }

    @PutMapping("/{taskIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public TaskResponse updateTask(@PathVariable String taskIdentifier, @RequestBody TaskRequest taskRequest) {
        return taskService.updateTask(taskIdentifier, taskRequest);
    }

    @DeleteMapping("/{taskIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable String taskIdentifier) {
        taskService.deleteTask(taskIdentifier);
    }

}
