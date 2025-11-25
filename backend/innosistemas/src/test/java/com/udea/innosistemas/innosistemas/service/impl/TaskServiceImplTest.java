package com.udea.innosistemas.innosistemas.service.impl;

import com.udea.innosistemas.innosistemas.entity.*;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.repository.TaskRepository;
import com.udea.innosistemas.innosistemas.service.NotificationProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @Mock
    TaskRepository taskRepository;
    @Mock
    StateRepository stateRepository;
    @Mock
    NotificationProducer notificationProducer;
    @InjectMocks
    TaskServiceImpl taskServiceImpl;

    @Test
    void testDeleteTask() {
        when(taskRepository.existsById(0L)).thenReturn(true);
        taskServiceImpl.deleteTask(0L);

        verify(taskRepository).deleteById(0L);
    }

    @Test
    void testSaveTask() {
        Task task = new Task();
        State state = new State();

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        Task result = taskServiceImpl.saveTask(task);

        assertEquals(task, result);
    }

    @Test
    void testListAllTasks() {
        List<Task> tasks = List.of(new Task());

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskServiceImpl.listAllTasks();

        assertEquals(tasks, result);
    }

    @Test
    void testUpdateState() {
        Task task = new Task();
        State state = new State();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(stateRepository.findById(anyLong())).thenReturn(Optional.of(state));

        taskServiceImpl.updateState(1L, 1L);

        verify(taskRepository).save(task);
    }

    @Test
    void testFindByDate() {
        List<Task> tasks = List.of(new Task());

        when(taskRepository.findByDeadlineBetween(any(), any())).thenReturn(tasks);

        List<Task> result = taskServiceImpl.findByDate(new Timestamp(0), new Timestamp(0));

        assertEquals(tasks, result);
    }


    @Test
    void testSendNotification() {
        Task t = new Task();
        Project p = new Project();
        p.setName("Proyecto Test");
        t.setProject(p);
        t.setTitle("Tarea Test");
        t.setResponsible_email("test@example.com");
        List<Task> tasks = List.of(t);

        when(taskRepository.findByDeadlineBetween(any(), any())).thenReturn(tasks);

        taskServiceImpl.sendNotification();

        verify(notificationProducer).sendEmail(any(EmailEvent.class));
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme