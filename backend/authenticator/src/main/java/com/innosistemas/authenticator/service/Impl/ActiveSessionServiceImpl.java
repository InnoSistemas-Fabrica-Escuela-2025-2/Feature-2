package com.innosistemas.authenticator.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.innosistemas.authenticator.entity.ActiveSession;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.ActiveSessionRepository;
import com.innosistemas.authenticator.service.ActiveSessionService;

import jakarta.transaction.Transactional;

@Service
public class ActiveSessionServiceImpl implements ActiveSessionService {

    @Autowired
    private ActiveSessionRepository activeSessionRepository;

    @Override
    @Transactional
    public synchronized void registerSession(Person person, String token) {
        activeSessionRepository.findByPerson(person)
            .ifPresent(activeSessionRepository::delete);

        ActiveSession session = new ActiveSession();
        session.setToken(token);
        session.setPerson(person);
        activeSessionRepository.save(session);
    }
    

    @Override
    public void invalidateSession(Person person) {
        activeSessionRepository.findByPerson(person)
            .ifPresent(session -> activeSessionRepository.deleteById(session.getId()));
    }

    @Override
    public boolean isSessionActive(Person person) {
        return activeSessionRepository.findByPerson(person).isPresent();
    }
}