package com.udea.innosistemas.projects.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.projects.entity.Team;
import com.udea.innosistemas.projects.repository.TeamRepository;
import com.udea.innosistemas.projects.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService{

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public String nameTeam(Long id_student) {
        return handleNameTeam(id_student);
    }

    @Override
    public Long getTeamIdByStudent(Long id_student) {
        return handleGetTeamIdByStudent(id_student);
    }

    @Override
    public List<String> getStudentsNameById(Long id_student) {
        return handleGetStudentsNameById(id_student);
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
