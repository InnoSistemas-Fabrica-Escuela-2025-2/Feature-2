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
        if (!taskRepository.existsById(id)) {
            throw new NoSuchElementException("El usuario no existe.");
        }
        taskRepository.deleteById(id);
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
        Task task = getTaskOrThrow(id_task);
        State state = getStateOrThrow(id_state);

        task.setState(state);
        taskRepository.save(task);
    }

    private State resolveState(State requestedState) {
        return Optional.ofNullable(requestedState)
            .flatMap(this::tryResolveExplicitState)
            .orElseGet(this::resolveDefaultState);
    }

    private Optional<State> tryResolveExplicitState(State candidate) {
        return resolveById(candidate).or(() -> resolveByName(candidate));
    }

    private Optional<State> resolveById(State candidate) {
        Long requestedId = candidate.getId();
        if (requestedId == null) {
            return Optional.empty();
        }
        State state = stateRepository.findById(requestedId)
            .orElseThrow(() -> new NoSuchElementException("El estado especificado no existe."));
        return Optional.of(state);
    }

    private Optional<State> resolveByName(State candidate) {
        String normalizedName = normalizeName(candidate.getName());
        if (normalizedName.isEmpty()) {
            return Optional.empty();
        }
        return findByNameIgnoreCase(normalizedName);
    }

    private State resolveDefaultState() {
        Optional<State> pending = findByNameIgnoreCase("pendiente");
        if (pending.isPresent()) {
            return pending.get();
        }

        Optional<State> byDefaultId = stateRepository.findById(1L);
        if (byDefaultId.isPresent()) {
            return byDefaultId.get();
        }

        return findFirstStateOrThrow();
    }

    private State findFirstStateOrThrow() {
        return stateRepository.findAll().stream()
            .filter(s -> s.getId() != null)
            .min(Comparator.comparing(State::getId))
            .orElseThrow(() -> new NoSuchElementException("No hay estados configurados en el sistema."));
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private Optional<State> findByNameIgnoreCase(String name) {
        String normalizedName = normalizeName(name);
        if (normalizedName.isEmpty()) {
            return Optional.empty();
        }
        Optional<State> directMatch = stateRepository.findByNameIgnoreCase(normalizedName);
        if (directMatch.isPresent()) {
            return directMatch;
        }
        return stateRepository.findAll().stream()
            .filter(state -> state.getName() != null && state.getName().equalsIgnoreCase(normalizedName))
            .findFirst();
    }

    private Task getTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new NoSuchElementException("La tarea no existe."));
    }

    private State getStateOrThrow(Long stateId) {
        return stateRepository.findById(stateId)
            .orElseThrow(() -> new NoSuchElementException("El estado no existe."));
    }
}
