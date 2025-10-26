package com.innosistemas.authenticator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innosistemas.authenticator.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long>{
    Optional<Person> findByEmail(String email);
} 