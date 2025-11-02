package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/project/project")
public class ProjectController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private TeamRepository teamRepository;

    @GetMapping("/message")
    public ResponseEntity<String> showMesagge() {
        return ResponseEntity.ok("servicio 2 funcionando");
    }

    @PostMapping("/save")
    public ResponseEntity<Project> saveProject(@RequestBody Project project) {
       try {
            logger.info("🔵 INTENTANDO CREAR PROYECTO: '{}' - Timestamp: {}", 
                       project.getName(), System.currentTimeMillis());
            
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
            logger.info("✅ PROYECTO CREADO EXITOSAMENTE: ID={}, Nombre='{}', Timestamp: {}", 
                       saved.getId(), saved.getName(), System.currentTimeMillis());
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
