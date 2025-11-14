package com.udea.innosistemas.innosistemas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.Objective;
import com.udea.innosistemas.innosistemas.repository.ObjectiveRepository;
import com.udea.innosistemas.innosistemas.service.ObjectiveService;

@Service
public class ObjectiveServiceImpl implements ObjectiveService {

    private final ObjectiveRepository objectiveRepository;

    public ObjectiveServiceImpl(@Autowired ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    @Override
    public Objective saveObjective(Objective objective) {

        if (objective == null) {
            throw new IllegalArgumentException("No fue posible guardar el objetivo.");
        }
        return objectiveRepository.save(objective);
    }

}
