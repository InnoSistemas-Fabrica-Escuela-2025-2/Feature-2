package com.udea.innosistemas.innosistemas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.udea.innosistemas.innosistemas.entity.Project;
import com.udea.innosistemas.innosistemas.service.ProjectService;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private static final String PROJECT_NAME = "Proyecto Accesible";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    void messageEndpointReturnsHealthMessage() {
        try {
            mockMvc.perform(get("/project/project/message"))
                .andExpect(status().isOk())
                .andExpect(content().string("servicio 2 funcionando"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveProjectReturnsPersistedInstance() {
        Project project = new Project();
        project.setId(77L);
        project.setName(PROJECT_NAME);

        when(projectService.saveProject(any(Project.class))).thenReturn(project);

        // Arrange - realistic request body
        String json = "{\"name\":\"" + PROJECT_NAME + "\"}";

        // Act & Assert
        try {
            mockMvc.perform(post("/project/project/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(77))
                .andExpect(jsonPath("$.name").value(PROJECT_NAME));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectService).saveProject(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(PROJECT_NAME, captor.getValue().getName());
    }

    @Test
    void saveProjectReturnsErrorOnServiceFailure() {
        when(projectService.saveProject(any(Project.class))).thenThrow(new RuntimeException("fallo"));

        try {
            mockMvc.perform(post("/project/project/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listAllProjectsReturnsRepositoryData() {
        Project project = new Project();
        project.setId(12L);
        when(projectService.listAllProjects()).thenReturn(List.of(project));
        try {
            mockMvc.perform(get("/project/project/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(12));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listAllProjectsByIdReturnsStudentProjects() {
        Project project = new Project();
        project.setId(21L);
        when(projectService.listAllById(4L)).thenReturn(List.of(project));
        try {
            mockMvc.perform(get("/project/project/listAllById/{id}", 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(21));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
