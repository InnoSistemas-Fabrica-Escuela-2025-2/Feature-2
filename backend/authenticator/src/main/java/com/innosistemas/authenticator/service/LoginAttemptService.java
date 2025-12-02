package com.innosistemas.authenticator.service;

import com.innosistemas.authenticator.entity.Person;

public interface LoginAttemptService {
    void loginSucceeded(Person person);
    
    void loginFailed(Person person);

    boolean isBlocked(Person person);

    /**
     * Verifica si el usuario está bloqueado y, de estarlo, lanza una excepción con detalles.
     * No hace nada si no está bloqueado.
     */
    void checkBlocked(Person person);
}
