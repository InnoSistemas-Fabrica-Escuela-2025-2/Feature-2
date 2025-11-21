package com.udea.innosistemas.innosistemas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long>{
    
    @Query(value = "SELECT * FROM project.getnamebyidstudent(:student_id)", nativeQuery=true)
    String findNameByIdStudent(@Param("id_student") Long id_student);
    
    @Query(value = "SELECT * FROM public.getstudentsnamebyid(:teamName)", nativeQuery=true)
    List<String> getStudentsNameById(@Param("teamName") String teamName);
}
