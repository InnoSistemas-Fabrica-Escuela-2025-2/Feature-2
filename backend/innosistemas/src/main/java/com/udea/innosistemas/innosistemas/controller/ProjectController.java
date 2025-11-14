package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.udea.innosistemas.innosistemas.entity.Project;
import com.udea.innosistemas.innosistemas.service.ProjectService;
import java.util.Collections;




@RestController
@RequestMapping("/project/project")
public class ProjectController {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    
    
    private final ProjectService projectService;

    public ProjectController(@Autowired ProjectService projectService) {
        this.projectService = projectService;
    }

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
    public ResponseEntity<List<Project>> listAllProjects(
            @RequestHeader(value = "Role", required = false) String role,
            @RequestHeader(value = "Email", required = false) String email,
            @RequestHeader(value = "User-Id", required = false) String userId) {
        log.info("listAll called - Role: {}, Email: {}, UserId: {}", role, email, userId);
        try {
            if (isProfesor(role)) {
                return handleProfesorProjects();
            }
            if (isEstudiante(role, userId)) {
                return handleEstudianteProjects(userId);
            }
            return handleDefaultProjects();
        } catch (Exception e) {
            log.error("Error en listAll", e);
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.emptyList());
        }
    }

    private boolean isProfesor(String role) {
        return "profesor".equalsIgnoreCase(role);
    }

    private boolean isEstudiante(String role, String userId) {
        return "estudiante".equalsIgnoreCase(role) && userId != null;
    }

    private ResponseEntity<List<Project>> handleProfesorProjects() {
        log.info("Usuario es profesor, devolviendo todos los proyectos");
        return ResponseEntity.ok(projectService.listAllProjects());
    }

    private ResponseEntity<List<Project>> handleEstudianteProjects(String userId) {
        try {
            Long studentId = Long.valueOf(userId);
            log.info("Usuario es estudiante con ID: {}, obteniendo sus proyectos", studentId);
            List<Project> projects = projectService.listAllById(studentId);
            log.info("Devolviendo {} proyectos para el estudiante {}", projects.size(), studentId);
            return ResponseEntity.ok(projects);
        } catch (NumberFormatException e) {
            log.error("Error parseando userId: {}", userId, e);
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            log.error("Error obteniendo proyectos del estudiante", e);
            return ResponseEntity.ok(List.of());
        }
    }

    private ResponseEntity<List<Project>> handleDefaultProjects() {
        log.info("No hay rol específico, devolviendo todos los proyectos");
        return ResponseEntity.ok(projectService.listAllProjects());
    }

    @GetMapping("/listAllById/{id}")
    public ResponseEntity<List<Project>> listAllProjectsById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.listAllById(id));
    }
    
}
