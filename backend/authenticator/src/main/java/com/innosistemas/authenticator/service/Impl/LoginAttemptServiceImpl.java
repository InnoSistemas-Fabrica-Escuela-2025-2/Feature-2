package com.innosistemas.authenticator.service.Impl;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.innosistemas.authenticator.entity.LoginAttempt;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.LoginAttempsRepository;
import com.innosistemas.authenticator.service.LoginAttemptService;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService{
    private static final int MAX_FIRST_ATTEMPT = 5;
    private static final int MAX_SECOND_ATTEMPT = 2;
    private static final long BLOCK_TIME_MINUTES = 15;

    @Autowired 
    private LoginAttempsRepository loginAttempsRepository;

    @Override
    public void loginSucceeded(Person person) {
        loginAttempsRepository.findByPerson(person)
            .ifPresent(loginAttempsRepository::delete);
    }

    @Override
    public void loginFailed(Person person) {
        LoginAttempt loginAttempt = loginAttempsRepository.findByPerson(person)
             .orElseGet(() -> {
                LoginAttempt newAttempt = new LoginAttempt();
                newAttempt.setPerson(person);
                newAttempt.setAttempts(0);
                return newAttempt;
                });

        if (loginAttempt.getBlockUntil() != null &&
            loginAttempt.getBlockUntil().toLocalDateTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Usuario bloqueado hasta: " + loginAttempt.getBlockUntil());
        }

        loginAttempt.setAttempts(loginAttempt.getAttempts() + 1);
        loginAttempt.setLastAttempt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        if (loginAttempt.getAttempts() == MAX_FIRST_ATTEMPT) {
            loginAttempt.setBlockUntil(java.sql.Timestamp.valueOf(
                LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES)));
        } else if (loginAttempt.getAttempts() >= MAX_FIRST_ATTEMPT + MAX_SECOND_ATTEMPT) {
            loginAttempt.setBlockUntil(java.sql.Timestamp.valueOf(
                LocalDateTime.now().plusYears(100)));
        }

        loginAttempsRepository.save(loginAttempt);
    }

    @Override
    public boolean isBlocked(Person person) {
        return loginAttempsRepository.findByPerson(person)
        .filter(attempt -> attempt.getBlockUntil() != null &&
        attempt.getBlockUntil().toLocalDateTime().isAfter(LocalDateTime.now()))
        .isPresent();
    }
}
