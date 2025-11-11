package com.udea.innosistemas.innosistemas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.ServletException;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;
    private static final String TASK_TITLE = "Nueva tarea accesible";
    private static final String TASK_DESCRIPTION = "Descripción";

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void saveTaskReturnsPersistedPayload() {
        Task saved = new Task();
        saved.setId(99L);
    saved.setTitle(TASK_TITLE);
    saved.setDescription(TASK_DESCRIPTION);
        saved.setState(new State());

        when(taskService.saveTask(any(Task.class))).thenReturn(saved);

        // Arrange
    String json = "{\"title\":\"" + TASK_TITLE + "\",\"description\":\"" + TASK_DESCRIPTION + "\"}";

        // Act
        try {
            mockMvc.perform(post("/project/task/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.title").value(TASK_TITLE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).saveTask(captor.capture());
    org.junit.jupiter.api.Assertions.assertEquals(TASK_TITLE, captor.getValue().getTitle());
    }

    @Test
    void saveTaskReturnsErrorWhenServiceFails() {
        when(taskService.saveTask(any(Task.class))).thenThrow(new RuntimeException("fallo"));

        try {
            mockMvc.perform(post("/project/task/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listAllTasksDelegatesToService() {
        Task task = new Task();
        task.setId(5L);
        when(taskService.listAllTasks()).thenReturn(List.of(task));

        // Act & Assert
        try {
            mockMvc.perform(get("/project/task/listAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteTaskReturnsNoContent() {
        try {
            mockMvc.perform(delete("/project/task/delete/{id}", 7L))
                .andExpect(status().isNoContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(taskService).deleteTask(7L);
    }

    @Test
    void deleteTaskPropagatesServiceException() {
        doThrow(new UnsupportedOperationException("sin permiso")).when(taskService).deleteTask(8L);

        try {
            mockMvc.perform(delete("/project/task/delete/{id}", 8L)).andReturn();
            org.junit.jupiter.api.Assertions.fail("Expected ServletException to be thrown");
        } catch (ServletException se) {
            org.junit.jupiter.api.Assertions.assertTrue(se.getCause() instanceof UnsupportedOperationException);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateTaskReturnsPayloadWhenServiceSucceeds() {
        Task updated = new Task();
        updated.setId(11L);
        updated.setTitle("Título actualizado");
        when(taskService.saveTask(any(Task.class))).thenReturn(updated);

    String json = "{\"title\":\"Título actualizado\"}";

    try {
        mockMvc.perform(put("/project/task/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(11));
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    @Test
    void updateTaskReturnsErrorWhenServiceThrows() {
        when(taskService.saveTask(any(Task.class))).thenThrow(new RuntimeException("fallo"));

        try {
            mockMvc.perform(put("/project/task/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateStateReturnsNoContent() {
        try {
            mockMvc.perform(put("/project/task/updateState/{taskId}/{stateId}", 15L, 3L))
                .andExpect(status().isNoContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(taskService, times(1)).updateState(15L, 3L);
    }
}
