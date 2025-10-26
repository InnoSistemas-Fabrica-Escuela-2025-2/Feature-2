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

    // @Override
    // public List<Person> teamMembers(Long id_student) {
    //     List<person> teamMember = teamRepository.findAll(id_student)
    //     throw new UnsupportedOperationException("Unimplemented method 'teamMembers'");
    // }

    @Override
    public Long idTeam(Long id_student) {
        try{
            return teamRepository.findIdByIdStudent(id_student);
        } catch (Exception e){
            throw new UnsupportedOperationException("No fue posible encontrar el equipo.");
        }
    }

    @Override
    public List<Long> teamMembersId(Long id_team) {
        try{
            return teamRepository.findIdStudentById(id_team);
        } catch (Exception e){
            throw new UnsupportedOperationException("Unimplemented method 'teamMembersId'");
        }
    }   
}
