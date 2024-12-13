package com.jarnevermant.projectservice.repository;

import com.jarnevermant.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
