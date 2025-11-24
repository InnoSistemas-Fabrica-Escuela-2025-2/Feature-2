package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.udea.innosistemas.innosistemas.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/project/team")
public class TeamController {
    
    @Autowired
    // Servicio para manejar equipos
    private TeamService teamService;
    
    @Operation(
    summary = "Obtener los nombres de los estudiantes por id de equipo"
    )
    @GetMapping("/getStudentsName/{id}")
    public ResponseEntity<List<String>> getStudentsNameById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getStudentsNameById(id));
    }
}
