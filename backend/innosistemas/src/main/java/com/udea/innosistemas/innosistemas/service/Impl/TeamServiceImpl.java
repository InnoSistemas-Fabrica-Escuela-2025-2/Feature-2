package com.udea.innosistemas.innosistemas.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udea.innosistemas.innosistemas.repository.TeamRepository;
import com.udea.innosistemas.innosistemas.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService{

    @Autowired
    private TeamRepository teamRepository;

    //Obtener el nombre del equipo de un estudiante por su id
    @Override
    public String nameTeam(Long id_student) {
        try{
            return teamRepository.findNameByIdStudent(id_student);
        } catch (Exception e){
            throw new UnsupportedOperationException("No fue posible encontrar el equipo.");
        }
    }

    //Obtener los nombres de los estudiantes de un equipo por el id de un estudiante
    @Override
    public List<String> getStudentsNameById(Long id_student) {
        try{
            String teamName = nameTeam(id_student);
            return teamRepository.getStudentsNameById(teamName);
        } catch (Exception e){
            throw new UnsupportedOperationException("No existe el estudiante.");
        }
        
    }
}
