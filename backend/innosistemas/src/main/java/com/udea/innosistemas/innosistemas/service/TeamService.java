package com.udea.innosistemas.innosistemas.service;

import java.util.List;

public interface TeamService {
    
    String nameTeam(Long id_student);
    List<String> getStudentsNameById(Long id_student);
}
