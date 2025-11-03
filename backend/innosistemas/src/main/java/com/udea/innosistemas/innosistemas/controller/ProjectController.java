package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.Project;
import com.udea.innosistemas.innosistemas.service.ProjectService;



@RestController
@RequestMapping("/project/project")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @GetMapping("/message")
    public ResponseEntity<String> showMesagge() {
        return ResponseEntity.ok("servicio 2 funcionando");
    }

    @PostMapping("/save")
    public ResponseEntity<Project> saveProject(@RequestBody Project project) {
       try {
            Project saved = projectService.saveProject(project);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        } 
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<Project>> listAllProjects() {
        return ResponseEntity.ok(projectService.listAllProjects());
    }

    @GetMapping("/listAllById/{id}")
    public ResponseEntity<List<Project>> listAllProjectsById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.listAllById(id));
    }
    
}
