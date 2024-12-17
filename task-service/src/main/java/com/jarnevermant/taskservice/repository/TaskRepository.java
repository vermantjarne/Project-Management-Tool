package com.jarnevermant.taskservice.repository;

import com.jarnevermant.taskservice.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTaskIdentifier(String taskIdentifier);
    List<Task> findByStatus(String status);
    Optional<Task> findByTaskIdentifier(String taskIdentifier);
    List<Task> findByProject(String projectIdentifier);
    void deleteByTaskIdentifier(String taskIdentifier);

}
