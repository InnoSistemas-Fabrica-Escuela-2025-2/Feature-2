package com.udea.innosistemas.innosistemas.service.impl;

import java.util.List;

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

    private List<State> handleAllStates() {
        try {
            return stateRepository.findAll();
        } catch (Exception e) {
            throw new UnsupportedOperationException("No hay estados que mostrar.");
        }
    }
    
}
