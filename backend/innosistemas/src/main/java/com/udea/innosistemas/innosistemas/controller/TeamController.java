package com.udea.innosistemas.innosistemas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.udea.innosistemas.innosistemas.entity.Team;
import com.udea.innosistemas.innosistemas.service.TeamService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/project/team")
public class TeamController {
    

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }
    
    @GetMapping("/getStudentsName/{id}")
    public ResponseEntity<List<String>> getStudentsNameById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getStudentsNameById(id));
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<Team>> listAllTeams() {
        return ResponseEntity.ok(teamService.listAllTeams());
    }
}
