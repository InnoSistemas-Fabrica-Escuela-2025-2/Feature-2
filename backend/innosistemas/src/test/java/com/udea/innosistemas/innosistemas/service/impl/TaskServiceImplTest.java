package com.udea.innosistemas.innosistemas.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

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
        Task task = new Task();
        State defaultState = new State();
        defaultState.setId(3L);
        defaultState.setName("Pendiente");

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stateRepository.findByNameIgnoreCase("pendiente")).thenReturn(Optional.of(defaultState));

        Task savedTask = taskService.saveTask(task);

        assertNotNull(savedTask.getState(), "Se debe asignar un estado por defecto cuando no se envía desde la capa superior.");
        assertEquals(3L, savedTask.getState().getId());

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertEquals(3L, captor.getValue().getState().getId());
    }

    @Test
    void saveTaskPreservesProvidedState() {
        State initialState = new State();
        initialState.setId(5L);
        Task task = new Task();
        task.setState(initialState);

        when(taskRepository.save(task)).thenReturn(task);
        when(stateRepository.findById(5L)).thenReturn(Optional.of(initialState));

        Task savedTask = taskService.saveTask(task);

        assertSame(initialState, savedTask.getState());
        verify(taskRepository).save(task);
    }

    @Test
    void saveTaskWrapsRepositoryExceptions() {
        Task task = new Task();
        State defaultState = new State();
        defaultState.setId(1L);
        defaultState.setName("Pendiente");
        when(stateRepository.findByNameIgnoreCase("pendiente")).thenReturn(Optional.of(defaultState));
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
        when(stateRepository.findById(4L)).thenReturn(Optional.of(newState));

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
        when(stateRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.updateState(15L, 99L));
        assertNotNull(exception);
        verify(taskRepository, never()).save(any(Task.class));
    }
    }

