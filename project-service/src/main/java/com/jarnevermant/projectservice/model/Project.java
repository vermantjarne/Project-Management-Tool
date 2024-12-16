package com.jarnevermant.projectservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectIdentifier;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectLead;

}
