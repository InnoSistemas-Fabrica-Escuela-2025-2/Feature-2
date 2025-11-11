package com.udea.innosistemas.innosistemas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.service.Impl.StateServiceImpl;

@ExtendWith(MockitoExtension.class)
class StateServiceImplTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateServiceImpl stateService;

    @Test
    void allStatesReturnsRepositoryData() {
        State state = new State();
        List<State> states = List.of(state);
        when(stateRepository.findAll()).thenReturn(states);

        List<State> result = stateService.allStates();

        assertSame(states, result);
        verify(stateRepository).findAll();
    }

    @Test
    void allStatesWrapsRepositoryException() {
        when(stateRepository.findAll()).thenThrow(new RuntimeException("fallo"));

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, stateService::allStates);

        assertEquals("No hay estados que mostrar.", exception.getMessage());
    }
}
