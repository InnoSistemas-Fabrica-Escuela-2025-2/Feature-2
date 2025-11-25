package com.udea.innosistemas.innosistemas.service;

import java.util.List;

import com.udea.innosistemas.innosistemas.entity.Team;

public interface TeamService {
    
    String nameTeam(Long idStudent);
    Long getTeamIdByStudent(Long idStudent);
    List<String> getStudentsNameById(Long idStudent);
    List<String> getStudentsEmailById(Long teamId);
    List<Team> listAllTeams();
}
