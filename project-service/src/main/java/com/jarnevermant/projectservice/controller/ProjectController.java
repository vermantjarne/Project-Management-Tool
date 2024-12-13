package com.jarnevermant.projectservice.controller;

import com.jarnevermant.projectservice.dto.ProjectRequest;
import com.jarnevermant.projectservice.dto.ProjectResponse;
import com.jarnevermant.projectservice.model.Project;
import com.jarnevermant.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }

}
