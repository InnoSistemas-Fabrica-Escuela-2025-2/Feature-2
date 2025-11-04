package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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




@RestController
@RequestMapping("/project/project")
public class ProjectController {
    
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    
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
    public ResponseEntity<List<Project>> listAllProjects(
            @RequestHeader(value = "Role", required = false) String role,
            @RequestHeader(value = "Email", required = false) String email,
            @RequestHeader(value = "User-Id", required = false) String userId) {
        
        log.info("listAll called - Role: {}, Email: {}, UserId: {}", role, email, userId);
        
        try {
            // Si es profesor, devolver todos los proyectos
            if ("profesor".equalsIgnoreCase(role)) {
                log.info("Usuario es profesor, devolviendo todos los proyectos");
                return ResponseEntity.ok(projectService.listAllProjects());
            }
            
            // Si es estudiante y tenemos su ID, filtrar por sus proyectos
            if ("estudiante".equalsIgnoreCase(role) && userId != null) {
                try {
                    Long studentId = Long.parseLong(userId);
                    log.info("Usuario es estudiante con ID: {}, filtrando proyectos", studentId);
                    return ResponseEntity.ok(projectService.listAllById(studentId));
                } catch (NumberFormatException e) {
                    log.error("Error parseando userId: {}", userId, e);
                    return ResponseEntity.ok(List.of());
                }
            }
            
            // Por defecto, devolver todos (para casos sin autenticación o roles desconocidos)
            log.info("No hay rol específico, devolviendo todos los proyectos");
            return ResponseEntity.ok(projectService.listAllProjects());
        } catch (Exception e) {
            log.error("Error en listAll", e);
            throw e;
        }
    }

    @GetMapping("/listAllById/{id}")
    public ResponseEntity<List<Project>> listAllProjectsById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.listAllById(id));
    }
    
}
