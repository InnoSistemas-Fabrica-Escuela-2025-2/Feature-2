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

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.service.StateService;

@ExtendWith(MockitoExtension.class)
class StateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StateService stateService;

    @InjectMocks
    private StateController stateController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stateController).build();
    }

    @Test
    void listAllStates_returnsStates() throws Exception {
        State state = new State();
        state.setId(3L);
        state.setName("Completada");
        when(stateService.allStates()).thenReturn(List.of(state));

        mockMvc.perform(get("/project/state/listAll"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(3L))
            .andExpect(jsonPath("$[0].name").value("Completada"));

        verify(stateService).allStates();
    }
}
