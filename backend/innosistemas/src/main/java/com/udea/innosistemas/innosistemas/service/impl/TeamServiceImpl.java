package com.udea.innosistemas.innosistemas.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.Team;
import com.udea.innosistemas.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.innosistemas.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;

    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public String nameTeam(Long idStudent) {
        return handleNameTeam(idStudent);
    }

    @Override
    public Long getTeamIdByStudent(Long idStudent) {
        return handleGetTeamIdByStudent(idStudent);
    }

    @Override
    public List<String> getStudentsNameById(Long idStudent) {
        return handleGetStudentsNameById(idStudent);
    }

    @Override
    public List<Team> listAllTeams() {
        return handleListAllTeams();
    }

    private String handleNameTeam(Long idStudent) {
        try {
            return teamRepository.findNameByIdStudent(idStudent);
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible encontrar el equipo.");
        }
    }

    private Long handleGetTeamIdByStudent(Long idStudent) {
        try {
            return teamRepository.findTeamIdByStudent(idStudent);
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible encontrar el equipo del estudiante.");
        }
    }

    private List<String> handleGetStudentsNameById(Long idStudent) {
        try {
            String teamName = handleNameTeam(idStudent);
            return teamRepository.getStudentsNameById(teamName);
        } catch (Exception e) {
            throw new UnsupportedOperationException("No existe el estudiante.");
        }
    }

    private List<Team> handleListAllTeams() {
        try {
            return teamRepository.findAll();
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible obtener la lista de equipos.");
        }
    }
}
