package com.udea.innosistemas.innosistemas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long>{
    Long findIdByIdStudent(Long id_student);
    List<Long> findIdStudentById(Long id_team);
}
