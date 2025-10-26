package com.udea.innosistemas.innosistemas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udea.innosistemas.innosistemas.entity.Objective;
import com.udea.innosistemas.innosistemas.service.ObjectiveService;


@RestController
@RequestMapping("/objective")
public class ObjectiveController {

    @Autowired
    private ObjectiveService objectiveService;

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