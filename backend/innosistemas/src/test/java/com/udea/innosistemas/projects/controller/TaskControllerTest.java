package com.udea.innosistemas.projects.controller;

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

import com.udea.innosistemas.projects.entity.State;
import com.udea.innosistemas.projects.entity.Task;
import com.udea.innosistemas.projects.service.TaskService;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void saveTaskReturnsPersistedPayload() throws Exception {
        Task saved = new Task();
        saved.setId(99L);
        saved.setTitle("Nueva tarea accesible");
        saved.setDescription("Descripción");
        saved.setState(new State());

        when(taskService.saveTask(any(Task.class))).thenReturn(saved);

        // Arrange
        String json = "{\"title\":\"Nueva tarea accesible\",\"description\":\"Descripción\"}";

        // Act
        mockMvc.perform(post("/project/task/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            // Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.title").value("Nueva tarea accesible"));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).saveTask(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("Nueva tarea accesible", captor.getValue().getTitle());
    }

    @Test
    void saveTaskReturnsErrorWhenServiceFails() throws Exception {
        when(taskService.saveTask(any(Task.class))).thenThrow(new RuntimeException("fallo"));

        mockMvc.perform(post("/project/task/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void listAllTasksDelegatesToService() throws Exception {
        Task task = new Task();
        task.setId(5L);
        when(taskService.listAllTasks()).thenReturn(List.of(task));

        // Act & Assert
        mockMvc.perform(get("/project/task/listAll"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(5));
    }

    @Test
    void deleteTaskReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/project/task/delete/{id}", 7L))
            .andExpect(status().isNoContent());

        verify(taskService).deleteTask(7L);
    }

    @Test
    void deleteTaskPropagatesServiceException() throws Exception {
        doThrow(new UnsupportedOperationException("sin permiso")).when(taskService).deleteTask(8L);

        ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(ServletException.class, () ->
            mockMvc.perform(delete("/project/task/delete/{id}", 8L)).andReturn()
        );

        org.junit.jupiter.api.Assertions.assertTrue(exception.getCause() instanceof UnsupportedOperationException);
    }

    @Test
    void updateTaskReturnsPayloadWhenServiceSucceeds() throws Exception {
        Task updated = new Task();
        updated.setId(11L);
        updated.setTitle("Título actualizado");
        when(taskService.saveTask(any(Task.class))).thenReturn(updated);

    String json = "{\"title\":\"Título actualizado\"}";

    mockMvc.perform(put("/project/task/update")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(11));
    }

    @Test
    void updateTaskReturnsErrorWhenServiceThrows() throws Exception {
        when(taskService.saveTask(any(Task.class))).thenThrow(new RuntimeException("fallo"));

        mockMvc.perform(put("/project/task/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void updateStateReturnsNoContent() throws Exception {
        mockMvc.perform(put("/project/task/updateState/{taskId}/{stateId}", 15L, 3L))
            .andExpect(status().isNoContent());

        verify(taskService, times(1)).updateState(15L, 3L);
    }
}
