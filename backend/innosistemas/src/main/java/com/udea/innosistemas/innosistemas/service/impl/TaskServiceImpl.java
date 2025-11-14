package com.udea.innosistemas.innosistemas.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.repository.TaskRepository;
import com.udea.innosistemas.innosistemas.service.StateService;
import com.udea.innosistemas.innosistemas.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final StateService stateService;

    public TaskServiceImpl(TaskRepository taskRepository, StateService stateService) {
        this.taskRepository = taskRepository;
        this.stateService = stateService;
    }

    @Override
    public void deleteTask(long id) {
        if (!taskRepository.existsById(id)) {
            throw new NoSuchElementException("El usuario no existe.");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public Task saveTask(Task task) {
        return handleSaveTask(task);
    }

    private Task handleSaveTask(Task task) {
        try {
            State resolvedState = stateService.resolveState(task.getState());
            task.setState(resolvedState);
            return taskRepository.save(task);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new NoSuchElementException("No fue posible guardar la tarea.");
        }
    }

    @Override
    public List<Task> listAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void updateState(Long idTask, Long idState) {
        log.info("[TaskService] updateState called with taskId={} stateId={}", idTask, idState);
        Task task = getTaskOrThrow(idTask);
        State state = getStateOrThrow(idState);
        log.info("[TaskService] resolved state: id={} name={}", state.getId(), state.getName());

        task.setState(state);
        taskRepository.save(task);
    }

    private Task getTaskOrThrow(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser null");
        }
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("La tarea no existe."));
    }

    private State getStateOrThrow(Long stateId) {
        if (stateId == null) {
            throw new IllegalArgumentException("El ID del estado no puede ser null");
        }
        return stateService.findById(stateId)
                .orElseThrow(() -> new NoSuchElementException("El estado no existe."));
    }
}
