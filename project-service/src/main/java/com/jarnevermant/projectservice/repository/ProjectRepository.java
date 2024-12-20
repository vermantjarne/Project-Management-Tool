package com.jarnevermant.projectservice.repository;

import com.jarnevermant.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByProjectIdentifier(String projectIdentifier);
    List<Project> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
    Optional<Project> findByProjectIdentifier(String projectIdentifier);
    void deleteByProjectIdentifier(String projectIdentifier);

}
