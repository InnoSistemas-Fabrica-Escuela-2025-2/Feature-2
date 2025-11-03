package com.innosistemas.authenticator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.innosistemas.authenticator.entity.ActiveSession;
import com.innosistemas.authenticator.entity.Person;

@Repository
public interface ActiveSessionRepository extends JpaRepository<ActiveSession, Long> {
    Optional<ActiveSession> findByPerson(Person person);
    Optional<ActiveSession> findByToken(String token);
}
