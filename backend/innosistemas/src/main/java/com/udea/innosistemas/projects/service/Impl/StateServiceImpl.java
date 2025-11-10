package com.udea.innosistemas.projects.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.projects.entity.State;
import com.udea.innosistemas.projects.repository.StateRepository;
import com.udea.innosistemas.projects.service.StateService;

@Service
public class StateServiceImpl implements StateService{

    @Autowired
    private StateRepository stateRepository;

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
