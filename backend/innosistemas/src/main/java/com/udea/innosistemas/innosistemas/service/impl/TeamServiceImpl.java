package com.udea.innosistemas.innosistemas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.entity.Team;
import com.udea.innosistemas.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.innosistemas.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService{

    @Autowired
    // Repositorio para manejar equipos
    private TeamRepository teamRepository;

    @Override
    //Obtener el nombre del equipo de un estudiante por su id
    public String nameTeam(Long id_student) {
        try{
            return teamRepository.findNameByIdStudent(id_student);
        } catch (Exception e){
            throw new UnsupportedOperationException("No fue posible encontrar el equipo.");
        }
    }

    @Override
    //Obtener los nombres de los estudiantes de un equipo por el id de un estudiante
    public List<String> getStudentsNameById(Long id_student) {
        try{
            String teamName = nameTeam(id_student);
            return teamRepository.getStudentsNameById(teamName);
        } catch (Exception e){
            throw new UnsupportedOperationException("No existe el estudiante.");
        }
        
    }

    @Override
    public List<Team> listAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Long getTeamIdByStudent(Long idStudent) {
        try {
            String teamName = nameTeam(idStudent);
            return listAllTeams().stream()
                .filter(team -> team.getName().equals(teamName))
                .map(Team::getId)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Equipo no encontrado."));
        } catch (Exception e) {
            throw new UnsupportedOperationException("No fue posible encontrar el equipo del estudiante.");
        }
    }
}
