package com.udea.innosistemas.innosistemas.service.Impl;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.entity.Task;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.repository.TaskRepository;
import com.udea.innosistemas.innosistemas.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StateRepository stateRepository;

    @Override
    public void deleteTask(long id) {
        if(taskRepository.existsById(id)){
            taskRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("El usuario no existe.");
        }
    }

    @Override
    public Task saveTask(Task task) {
        try {
            State resolvedState = resolveState(task.getState());
            task.setState(resolvedState);
            return taskRepository.save(task);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e){
            throw new NoSuchElementException("No fue posible guardar la tarea.");
        }
    }

    @Override
    public List<Task> listAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void updateState(Long id_task, Long id_state) {
        Task task = taskRepository.findById(id_task)
            .orElseThrow(() -> new NoSuchElementException("La tarea no existe."));

        State state = stateRepository.findById(id_state)
            .orElseThrow(() -> new NoSuchElementException("El estado no existe."));

        task.setState(state);
        taskRepository.save(task);
    }

    private State resolveState(State requestedState) {
        if (requestedState != null) {
            Long requestedId = requestedState.getId();
            if (requestedId != null) {
                return stateRepository.findById(requestedId)
                    .orElseThrow(() -> new NoSuchElementException("El estado especificado no existe."));
            }

            String requestedName = requestedState.getName();
            if (requestedName != null && !requestedName.trim().isEmpty()) {
                Optional<State> byName = stateRepository.findByNameIgnoreCase(requestedName.trim());
                if (byName.isPresent()) {
                    return byName.get();
                }
            }
        }

        return stateRepository.findByNameIgnoreCase("pendiente")
            .or(() -> stateRepository.findById(1L))
            .orElseGet(() -> stateRepository.findAll().stream()
                .filter(s -> s.getId() != null)
                .min(Comparator.comparing(State::getId))
                .orElseThrow(() -> new NoSuchElementException("No hay estados configurados en el sistema.")));
    }
}
