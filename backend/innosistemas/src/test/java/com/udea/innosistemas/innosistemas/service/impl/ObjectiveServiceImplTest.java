package com.udea.innosistemas.innosistemas.service.impl;

import com.udea.innosistemas.innosistemas.entity.*;
import com.udea.innosistemas.innosistemas.repository.ObjectiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ObjectiveServiceImplTest {
    @Mock
    ObjectiveRepository objectiveRepository;
    @InjectMocks
    ObjectiveServiceImpl objectiveServiceImpl;

     Objective objective = new Objective();

    @Test
    void testSaveObjective() {
        Objective input = new Objective(1L, "description", null);

        Objective saved = new Objective(1L, "description", null);

        when(objectiveRepository.save(any(Objective.class))).thenReturn(saved);

        Objective result = objectiveServiceImpl.saveObjective(input);

        Assertions.assertEquals(saved.getId(), result.getId());
        Assertions.assertEquals(saved.getDescription(), result.getDescription());
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme