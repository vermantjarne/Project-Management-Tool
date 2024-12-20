package com.jarnevermant.projectservice.controller;

import com.jarnevermant.projectservice.dto.ProjectRequest;
import com.jarnevermant.projectservice.dto.ProjectResponse;
import com.jarnevermant.projectservice.model.Project;
import com.jarnevermant.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createProject(@RequestBody ProjectRequest projectRequest) {
        projectService.createProject(projectRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectResponse> getActiveProjects() {
        return projectService.getActiveProjects();
    }

    @GetMapping("/{projectIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponse getProject(@PathVariable String projectIdentifier) {
        return projectService.getProject(projectIdentifier);
    }

    @PutMapping("/{projectIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponse updateProject(@PathVariable String projectIdentifier, @RequestBody ProjectRequest projectRequest) {
        return projectService.updateProject(projectIdentifier, projectRequest);
    }

    @DeleteMapping("/{projectIdentifier}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProject(@PathVariable String projectIdentifier) {
        projectService.deleteProject(projectIdentifier);
    }

}
