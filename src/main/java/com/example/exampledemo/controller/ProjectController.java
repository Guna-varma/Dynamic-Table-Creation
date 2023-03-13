package com.example.exampledemo.controller;

import com.example.exampledemo.Repository.ProjectRepo;
import com.example.exampledemo.model.Project;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class ProjectController {

    @Autowired
    private ProjectRepo repo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    protected EntityManager em;


    @PostMapping("/addProject")
    private Project addProject(@RequestBody Project project){
        return repo.save(project);
    }

}