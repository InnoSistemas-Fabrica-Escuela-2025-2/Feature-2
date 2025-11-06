package com.udea.innosistemas.innosistemas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.State;


@Repository
public interface StateRepository extends JpaRepository<State,Long>{
    @Override
    public List<State> findAll(); 

    Optional<State> findByNameIgnoreCase(String name);
}
