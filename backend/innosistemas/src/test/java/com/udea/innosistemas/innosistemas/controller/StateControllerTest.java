package com.udea.innosistemas.innosistemas.controller;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.service.StateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class StateControllerTest {
    @Mock
    StateService stateService;
    @InjectMocks
    StateController stateController;

    @Test
    void testAllStates() {
        when(stateService.allStates()).thenReturn(List.of(new State(1L, "name")));

        ResponseEntity<List<State>> result = stateController.allStates();
        Assertions.assertEquals(1, result.getBody().size());
        Assertions.assertEquals(1L, result.getBody().get(0).getId());
        Assertions.assertEquals("name", result.getBody().get(0).getName());
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme