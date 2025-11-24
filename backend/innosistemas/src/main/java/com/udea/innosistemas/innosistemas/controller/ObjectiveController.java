package com.udea.innosistemas.innosistemas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.Objective;
import com.udea.innosistemas.innosistemas.service.ObjectiveService;

import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/project/objective")
public class ObjectiveController {

    @Autowired
    // Servicio para manejar objetivos
    private ObjectiveService objectiveService;


    @Operation(
    summary = "Guardar un objetivo"
    )
    @PostMapping("/save")
    public ResponseEntity<Objective> saveObjective(@RequestBody Objective objective) {
        try {
            Objective saved = objectiveService.saveObjective(objective);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        } 
    }
    
}