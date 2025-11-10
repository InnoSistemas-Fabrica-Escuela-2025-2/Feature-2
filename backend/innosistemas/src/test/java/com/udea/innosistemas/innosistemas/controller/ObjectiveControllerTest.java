package com.udea.innosistemas.innosistemas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.udea.innosistemas.innosistemas.entity.Objective;
import com.udea.innosistemas.innosistemas.service.ObjectiveService;

@ExtendWith(MockitoExtension.class)
class ObjectiveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ObjectiveService objectiveService;

    @InjectMocks
    private ObjectiveController objectiveController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(objectiveController).build();
    }

    @Test
    void saveObjectiveReturnsSavedEntity() {
        // Arrange
        Objective objective = new Objective();
        objective.setId(3L);
        objective.setDescription("Accesibilidad mejorada");

        when(objectiveService.saveObjective(any(Objective.class))).thenReturn(objective);

        String json = "{\"description\":\"Accesibilidad mejorada\"}";

        // Act & Assert
        try {
            mockMvc.perform(post("/project/objective/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("Accesibilidad mejorada"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void saveObjectiveReturnsErrorWhenServiceFails() {
        when(objectiveService.saveObjective(any(Objective.class))).thenThrow(new RuntimeException("fallo"));

        try {
            mockMvc.perform(post("/project/objective/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
