package com.udea.innosistemas.innosistemas.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.udea.innosistemas.innosistemas.service.TeamService;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
    }

    @Test
    void getStudentsName_returnsTeamMembers() throws Exception {
        when(teamService.getStudentsNameById(10L)).thenReturn(List.of("María", "Juan"));

        mockMvc.perform(get("/project/team/getStudentsName/{id}", 10L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("María"))
            .andExpect(jsonPath("$[1]").value("Juan"));

        verify(teamService).getStudentsNameById(10L);
    }
}
