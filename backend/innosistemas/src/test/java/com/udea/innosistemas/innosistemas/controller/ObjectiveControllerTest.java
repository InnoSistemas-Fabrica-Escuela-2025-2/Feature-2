package com.udea.innosistemas.innosistemas.controller;

import com.udea.innosistemas.innosistemas.entity.*;
import com.udea.innosistemas.innosistemas.service.ObjectiveService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ObjectiveControllerTest {
    @Mock
    ObjectiveService objectiveService;
    @InjectMocks
    ObjectiveController objectiveController;



    @Test
    void testSaveObjective() {
        Objective expected = new Objective(1L, "description", new Project(1L, "name", "description", new Timestamp(0), List.of(new Task(1L, "title", "description", new Timestamp(0), "responsible_email", null, new State(1L, "name"))), List.of(), new Team(0L, "name")));
        when(objectiveService.saveObjective(any(Objective.class))).thenReturn(expected);

        ResponseEntity<Objective> result = objectiveController.saveObjective(expected);
        Assertions.assertEquals(ResponseEntity.ok(expected), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme