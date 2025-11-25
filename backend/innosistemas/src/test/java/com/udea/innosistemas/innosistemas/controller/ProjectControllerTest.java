package com.udea.innosistemas.innosistemas.controller;

import com.udea.innosistemas.innosistemas.entity.*;
import com.udea.innosistemas.innosistemas.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    ProjectService projectService;
    @InjectMocks
    ProjectController projectController;

    @Test
    void testShowMesagge() {
        ResponseEntity<String> result = projectController.showMesagge();
        Assertions.assertEquals(ResponseEntity.ok("servicio 2 funcionando"), result);
    }

    @Test
    void testSaveProject() {
        Project p = new Project(1L, "name", "description", new Timestamp(0), List.of(new Task(1L, "title", "description", new Timestamp(0), "responsible_email", null, new State(1L, "name"))), List.of(new Objective(1L, "description", null)), new Team(0L, "name"));
        when(projectService.saveProject(any(Project.class))).thenReturn(p);

        ResponseEntity<Project> result = projectController.saveProject(p);
        Assertions.assertEquals(ResponseEntity.ok(p), result);
    }

    @Test
    void testListAllProjects() {
        Project p2 = new Project(1L, "name", "description", new Timestamp(0), List.of(new Task(1L, "title", "description", new Timestamp( 0), "responsible_email", null, new State(1L, "name"))), List.of(new Objective(1L, "description", null)), new Team(0L, "name"));
        when(projectService.listAllProjects()).thenReturn(List.of(p2));

        ResponseEntity<List<Project>> result = projectController.listAllProjects();
        Assertions.assertEquals(ResponseEntity.ok(List.of(p2)), result);
    }

    @Test
    void testListAllProjectsById() {
        Project p3 = new Project(1L, "name", "description", new Timestamp(0), List.of(new Task(1L, "title", "description", new Timestamp( 0), "responsible_email", null, new State(1L, "name"))), List.of(new Objective(1L, "description", null)), new Team(0L, "name"));
        when(projectService.listAllById(anyLong())).thenReturn(List.of(p3));

        ResponseEntity<List<Project>> result = projectController.listAllProjectsById(1L);
        Assertions.assertEquals(ResponseEntity.ok(List.of(p3)), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme