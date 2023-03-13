package com.example.exampledemo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Transient
    private List<Project> projectList;
}