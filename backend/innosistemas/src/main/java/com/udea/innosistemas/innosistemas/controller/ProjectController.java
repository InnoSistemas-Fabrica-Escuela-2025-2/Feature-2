package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.Project;
import com.udea.innosistemas.innosistemas.entity.Team;
import com.udea.innosistemas.innosistemas.service.ProjectService;
import com.udea.innosistemas.innosistemas.repository.TeamRepository;


@RestController
@RequestMapping("/project")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private TeamRepository teamRepository;

    @PostMapping("/save")
    public ResponseEntity<Project> saveProject(@RequestBody Project project) {
       try {
            // Si no tiene team asignado, asignar el primero disponible
            if (project.getTeam() == null) {
                List<Team> teams = teamRepository.findAll();
                if (!teams.isEmpty()) {
                    project.setTeam(teams.get(0));
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
            
            Project saved = projectService.saveProject(project);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } 
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<Project>> listAllTasks() {
        return ResponseEntity.ok(projectService.listAllProjects());
    }
    
}
