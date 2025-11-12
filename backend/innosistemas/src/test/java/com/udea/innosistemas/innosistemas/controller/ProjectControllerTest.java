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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    void messageEndpointReturnsHealthMessage() throws Exception {
        mockMvc.perform(get("/project/project/message"))
            .andExpect(status().isOk())
            .andExpect(content().string("servicio 2 funcionando"));
    }

    @Test
    void saveProjectReturnsPersistedInstance() throws Exception {
        Project project = new Project();
        project.setId(77L);
    project.setName("Proyecto Accesible");

        when(projectService.saveProject(any(Project.class))).thenReturn(project);

        mockMvc.perform(post("/project/project/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(77L))
            .andExpect(jsonPath("$.name").value("Proyecto Accesible"));

        verify(projectService).saveProject(any(Project.class));
    }

    @Test
    void saveProjectReturnsErrorOnServiceFailure() throws Exception {
        when(projectService.saveProject(any(Project.class))).thenThrow(new RuntimeException("fallo"));

        mockMvc.perform(post("/project/project/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void listAllProjectsReturnsRepositoryData() throws Exception {
        Project project = new Project();
        project.setId(12L);
        when(projectService.listAllProjects()).thenReturn(List.of(project));

        mockMvc.perform(get("/project/project/listAll"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(12L));
    }

    @Test
    void listAllProjectsByIdReturnsStudentProjects() throws Exception {
        Project project = new Project();
        project.setId(21L);
        when(projectService.listAllById(4L)).thenReturn(List.of(project));

        mockMvc.perform(get("/project/project/listAllById/{id}", 4L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(21L));
    }
}
