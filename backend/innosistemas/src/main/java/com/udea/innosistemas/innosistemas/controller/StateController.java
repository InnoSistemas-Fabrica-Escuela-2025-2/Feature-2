package com.udea.innosistemas.innosistemas.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.State;
import com.udea.innosistemas.innosistemas.service.StateService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/project/state")
public class StateController {
    
    @Autowired
    // Servicio para manejar estados
    private StateService stateService;

    @GetMapping("/listAll")
    // Endpoint para listar todos los estados
    public ResponseEntity<List<State>> allStates() {
        return ResponseEntity.ok(stateService.allStates());
    }
    
}
