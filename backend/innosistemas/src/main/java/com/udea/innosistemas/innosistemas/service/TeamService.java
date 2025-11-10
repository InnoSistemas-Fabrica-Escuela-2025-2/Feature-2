package com.udea.innosistemas.innosistemas.service;

import java.util.List;

import com.udea.innosistemas.innosistemas.entity.Team;

public interface TeamService {
    
    String nameTeam(Long id_student);
    Long getTeamIdByStudent(Long id_student);
    List<String> getStudentsNameById(Long id_student);
    List<Team> listAllTeams();
}
