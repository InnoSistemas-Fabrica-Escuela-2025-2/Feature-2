package com.innosistemas.authenticator.service;

import com.innosistemas.authenticator.entity.Person;

public interface ActiveSessionService {
    void registerSession(Person person, String token);

    void invalidateSession(Person person);

    boolean isSessionActive(Person person);

}
