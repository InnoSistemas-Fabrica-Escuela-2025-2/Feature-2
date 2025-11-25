package com.udea.innosistemas.innosistemas.controller;

import com.udea.innosistemas.innosistemas.entity.*;
import com.udea.innosistemas.innosistemas.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Mock
    TaskService taskService;
    @InjectMocks
    TaskController taskController;



    @Test
    void testSaveTask() {
        Task expected = new Task(1L, "title", "description", new Timestamp(0, 0, 0, 0, 0, 0, 0), "responsible_email", new Project(1L, "name", "description", new Timestamp(0, 0, 0, 0, 0, 0, 0), List.of(), List.of(new Objective(1L, "description", null)), new Team(0L, "name")), new State(1L, "name"));
        when(taskService.saveTask(any(Task.class))).thenReturn(expected);

        ResponseEntity<Task> result = taskController.saveTask(expected);
        Assertions.assertEquals(ResponseEntity.ok(expected), result);
    }

    @Test
    void testListAllTasks() {
        Task t = new Task(1L, "title", "description", new Timestamp(0, 0, 0, 0, 0, 0, 0), "responsible_email", new Project(1L, "name", "description", new Timestamp(0, 0, 0, 0, 0, 0, 0), List.of(), List.of(new Objective(1L, "description", null)), new Team(0L, "name")), new State(1L, "name"));
        when(taskService.listAllTasks()).thenReturn(List.of(t));

        ResponseEntity<List<Task>> result = taskController.listAllTasks();
        Assertions.assertEquals(ResponseEntity.ok(List.of(t)), result);
    }

    @Test
    void testDeleteTask() {
        ResponseEntity<Void> result = taskController.deleteTask(1L);
        verify(taskService).deleteTask(anyLong());
        Assertions.assertEquals(ResponseEntity.noContent().build(), result);
    }

    @Test
    void testUpdatedTask() {
        Task expected = new Task(1L, "title", "description", new Timestamp(0, 0, 0, 0, 0, 0, 0), "responsible_email", new Project(1L, "name", "description", new Timestamp(0, 0, 0, 0, 0, 0, 0), List.of(), List.of(new Objective(1L, "description", null)), new Team(0L, "name")), new State(1L, "name"));
        when(taskService.saveTask(any(Task.class))).thenReturn(expected);

        ResponseEntity<Task> result = taskController.updatedTask(expected);
        Assertions.assertEquals(ResponseEntity.ok(expected), result);
    }

    @Test
    void testUpdateState() {
        ResponseEntity<Void> result = taskController.updateState(1L, 1L);
        verify(taskService).updateState(anyLong(), anyLong());
        Assertions.assertEquals(ResponseEntity.noContent().build(), result);
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme