package com.udea.innosistemas.innosistemas.service;

import java.util.List;

public interface TeamService {
    
    Long idTeam(Long id_student);
    List<Long> teamMembersId(Long id_team);
    //List<Person> teamMembers(Long id_student); 
}
