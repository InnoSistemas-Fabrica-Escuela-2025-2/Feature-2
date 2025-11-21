package com.udea.innosistemas.innosistemas.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.repository.StateRepository;
import com.udea.innosistemas.innosistemas.service.StateService;

@Service
public class StateServiceImpl implements StateService{

    @Autowired
    private StateRepository stateRepository;

    //Obtener todos los estados
    @Override
    public List<State> allStates() {
        try {
            return stateRepository.findAll();
        } catch (Exception e){
            throw new UnsupportedOperationException("No hay estados que mostrar.");
        }
    }
    
}
