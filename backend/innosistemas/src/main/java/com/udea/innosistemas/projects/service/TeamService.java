package com.udea.innosistemas.projects.service;

import java.util.List;

import com.udea.innosistemas.projects.entity.Team;

public interface TeamService {
    
    String nameTeam(Long id_student);
    Long getTeamIdByStudent(Long id_student);
    List<String> getStudentsNameById(Long id_student);
    List<Team> listAllTeams();
}
