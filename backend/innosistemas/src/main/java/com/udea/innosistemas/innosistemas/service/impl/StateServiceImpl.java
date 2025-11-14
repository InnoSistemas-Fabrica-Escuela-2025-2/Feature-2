package com.udea.innosistemas.innosistemas.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.service.StateService;

@Service
public class StateServiceImpl implements StateService{

    private final StateRepository stateRepository;
    
    @Autowired
    public StateServiceImpl(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public List<State> allStates() {
        return handleAllStates();
    }

    @Override
    public Optional<State> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return stateRepository.findById(id);
    }

    @Override
    public Optional<State> findByNameIgnoreCase(String name) {
        return Optional.ofNullable(name)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(n -> {
                    Optional<State> direct = stateRepository.findByNameIgnoreCase(n);
                    if (direct.isPresent()) {
                        return direct;
                    }
                    return stateRepository.findAll().stream()
                            .filter(state -> state.getName() != null && state.getName().equalsIgnoreCase(n))
                            .findFirst();
                });
    }

    @Override
    public State resolveState(State requested) {
        return Optional.ofNullable(requested)
                .flatMap(this::tryResolveExplicitState)
                .orElseGet(this::resolveDefaultState);
    }

    @Override
    public State findDefaultState() {
        return resolveDefaultState();
    }

    private List<State> handleAllStates() {
        try {
            return stateRepository.findAll();
        } catch (Exception e) {
            throw new UnsupportedOperationException("No hay estados que mostrar.");
        }
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

    // The public method findByNameIgnoreCase above already covers repository lookup and
    // fallback; no private duplicate is needed.
    
}
