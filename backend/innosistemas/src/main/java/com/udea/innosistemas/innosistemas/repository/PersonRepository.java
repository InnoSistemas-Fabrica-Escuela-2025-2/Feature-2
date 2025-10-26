package com.udea.innosistemas.innosistemas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.udea.innosistemas.innosistemas.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long>{
} 