package com.udea.innosistemas.innosistemas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.service.StateService;
import com.udea.innosistemas.innosistemas.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StateService stateService; // use service mock, not repository

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        // common fixtures
        State defaultState = new State();
        defaultState.setId(1L);
        defaultState.setName("pendiente");

        State providedState = new State();
        providedState.setId(2L);
        providedState.setName("en-progreso");

        // Ensure resolveState behaves as expected for tests: if incoming state is null or
        // has no id/name, return defaultState; if it has id==2L return providedState.
        lenient().when(stateService.resolveState(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    State arg = invocation.getArgument(0);
                    if (arg == null) {
                        return defaultState;
                    }
                    Long id = arg.getId();
                    String name = arg.getName();
                    if (id != null && id.equals(2L)) {
                        return providedState;
                    }
                    if ((id == null) && (name == null || name.trim().isEmpty())) {
                        return defaultState;
                    }
                    // fallback: return providedState when id==2, otherwise default
                    return defaultState;
                });

        // Ensure repository save returns the passed task (by default) so service returns it
        lenient().when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // If tests previously stubbed repository methods that are no longer called,
        // remove those stubbings instead of keeping them.
    }

    @Test
    void deleteTaskRemovesExistingTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTaskThrowsWhenTaskDoesNotExist() {
        when(taskRepository.existsById(2L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.deleteTask(2L));

        assertEquals("El usuario no existe.", exception.getMessage());
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void saveTaskAssignsDefaultStateWhenMissing() {
        // arrange: create task without state
        Task input = new Task();
        input.setTitle("t1");
        input.setState(null);

    // act: call saveTask
    Task result = taskService.saveTask(input);

        // assert: default state assigned
        assertNotNull(result.getState());
        assertEquals("pendiente", result.getState().getName());
    }

    @Test
    void saveTaskPreservesProvidedState() {
        // arrange: create task with provided state id 2L
        Task input = new Task();
        input.setTitle("t2");
        State s = new State();
        s.setId(2L);
        input.setState(s);

    // act
    Task result = taskService.saveTask(input);

        // assert
        assertNotNull(result.getState());
        assertEquals(2L, result.getState().getId());
    }

    @Test
    void saveTaskWrapsRepositoryExceptions() {
        Task task = new Task();
        State defaultState = new State();
        defaultState.setId(1L);
        defaultState.setName("Pendiente");
        when(taskRepository.save(any(Task.class))).thenThrow(new RuntimeException("DB error"));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.saveTask(task));
        assertNotNull(exception);
    }

    @Test
    void listAllTasksReturnsRepositoryData() {
        Task task = new Task();
        List<Task> expected = Collections.singletonList(task);
        when(taskRepository.findAll()).thenReturn(expected);

        List<Task> result = taskService.listAllTasks();

        assertEquals(expected, result);
        verify(taskRepository).findAll();
    }

    @Test
    void updateStateUpdatesTaskWhenPresent() {
        Task existingTask = new Task();
        State newState = new State();
        newState.setId(4L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
    when(stateService.findById(4L)).thenReturn(Optional.of(newState));

        taskService.updateState(1L, 4L);

        assertSame(newState, existingTask.getState());
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateStateThrowsWhenTaskMissing() {
        when(taskRepository.findById(9L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.updateState(9L, 2L));
        assertNotNull(exception);
        verify(taskRepository, never()).save(any(Task.class));
    }
    @Test
    void updateStateThrowsWhenStateMissing() {
        Task existingTask = new Task();

        when(taskRepository.findById(15L)).thenReturn(Optional.of(existingTask));
    when(stateService.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.updateState(15L, 99L));
        assertNotNull(exception);
        verify(taskRepository, never()).save(any(Task.class));
    }
    }

