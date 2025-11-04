package com.udea.innosistemas.innosistemas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long>{
    
    @Query(value = "SELECT * FROM project.getprojectsbyid(:student_id)", nativeQuery=true)
    List<Long> getProjectsById(@Param("student_id") Long student_id);

    List<Project> findAllByIdIn(List<Long> id_projects);
    
    List<Project> findAllByTeamId(Long team_id);
}
