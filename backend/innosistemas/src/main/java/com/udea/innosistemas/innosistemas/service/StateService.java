package com.udea.innosistemas.innosistemas.service;

import java.util.List;
import java.util.Optional;

import com.udea.innosistemas.innosistemas.entity.State;

public interface StateService {
    List<State> allStates();
    Optional<State> findById(Long id);
    Optional<State> findByNameIgnoreCase(String name);
    State resolveState(State requested);
    State findDefaultState();
}
