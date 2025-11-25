package com.udea.innosistemas.innosistemas.service.impl;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class StateServiceImplTest {
    @Mock
    StateRepository stateRepository;
    @InjectMocks
    StateServiceImpl stateServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAllStates() {
        List<State> states = List.of(new State(1L, "name"));
        when(stateRepository.findAll()).thenReturn(states);
        List<State> result = stateServiceImpl.allStates();
        Assertions.assertEquals(states, result);
    }

    @Test
    void testFindById() {
        when(stateRepository.findById(anyLong())).thenReturn(Optional.empty());
        Optional<State> result = stateServiceImpl.findById(1L);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindByNameIgnoreCase() {
        when(stateRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        Optional<State> result = stateServiceImpl.findByNameIgnoreCase("name");
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindDefaultState() {
        State state = new State(1L, "name");
        when(stateRepository.findAll()).thenReturn(List.of(state));
        State result = stateServiceImpl.findDefaultState();
        Assertions.assertEquals(state, result);
    }

    @Test
    void testResolveState() {
        State state = new State(1L, "name");
        when(stateRepository.findAll()).thenReturn(List.of(state));
        State result = stateServiceImpl.resolveState(state);
        Assertions.assertEquals(state, result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme