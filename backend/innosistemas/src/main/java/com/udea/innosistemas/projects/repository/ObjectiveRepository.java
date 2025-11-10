package com.udea.innosistemas.projects.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.projects.entity.Objective;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective,Long>{
    
}