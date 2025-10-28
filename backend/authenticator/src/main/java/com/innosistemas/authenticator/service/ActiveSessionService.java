package com.innosistemas.authenticator.service;

import com.innosistemas.authenticator.entity.ActiveSession;
import com.innosistemas.authenticator.entity.Person;

public interface ActiveSessionService {
    void registerSession(Person person, String token);

    void invalidateSession(ActiveSession activeSession);

    boolean isSessionActive(Person person);

}
