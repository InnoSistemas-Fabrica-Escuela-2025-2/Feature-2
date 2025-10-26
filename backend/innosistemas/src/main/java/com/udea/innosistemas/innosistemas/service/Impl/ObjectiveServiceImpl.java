package com.udea.innosistemas.innosistemas.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.Objective;
import com.udea.innosistemas.innosistemas.repository.ObjectiveRepository;
import com.udea.innosistemas.innosistemas.service.ObjectiveService;

@Service
public class ObjectiveServiceImpl implements ObjectiveService{
    
    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Override
    public Objective saveObjective(Objective objective){
        return objectiveRepository.save(objective);
    }

}
