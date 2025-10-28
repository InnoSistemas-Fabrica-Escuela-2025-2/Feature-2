package com.innosistemas.authenticator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.innosistemas.authenticator.entity.LoginAttempt;
import com.innosistemas.authenticator.entity.Person;

@Repository
public interface LoginAttempsRepository extends JpaRepository<LoginAttempt, Long> {
    Optional<LoginAttempt> findByPerson(Person person);
}
