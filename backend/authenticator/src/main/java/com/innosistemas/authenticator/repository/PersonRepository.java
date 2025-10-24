package com.innosistemas.authenticator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.innosistemas.authenticator.entity.Person;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long>{
    Optional<Person> findByCorreoElectronico(String correoElectronico);
} 