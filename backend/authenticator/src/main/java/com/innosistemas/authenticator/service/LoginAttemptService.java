package com.innosistemas.authenticator.service;

import com.innosistemas.authenticator.entity.Person;

public interface LoginAttemptService {
    void loginSucceeded(Person person);
    
    void loginFailed(Person person);

    boolean isBlocked(Person person);
}
