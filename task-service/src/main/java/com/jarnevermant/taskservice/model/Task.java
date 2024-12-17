package com.jarnevermant.taskservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskIdentifier;

    private String name;
    private String description;
    private LocalDate dateCreated;
    private LocalDate deadline;
    private String status;

    private String project;
    private String employee;

}
