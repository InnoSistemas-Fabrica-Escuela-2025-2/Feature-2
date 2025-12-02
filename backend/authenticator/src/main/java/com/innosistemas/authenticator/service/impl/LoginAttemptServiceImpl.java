package com.innosistemas.authenticator.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import com.innosistemas.authenticator.entity.LoginAttempt;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.exception.AccountBlockedException;
import com.innosistemas.authenticator.exception.InvalidCredentialsException;
import com.innosistemas.authenticator.repository.LoginAttempsRepository;
import com.innosistemas.authenticator.service.LoginAttemptService;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService{
    private static final int MAX_FIRST_ATTEMPT = 5;
    private static final int MAX_SECOND_ATTEMPT = 2;
    private static final long BLOCK_TIME_MINUTES = 15;

    private final LoginAttempsRepository loginAttempsRepository;

    public LoginAttemptServiceImpl(LoginAttempsRepository loginAttempsRepository) {
        this.loginAttempsRepository = loginAttempsRepository;
    }

    @Override
    // Elimina el registro de intentos al iniciar sesión correctamente
    public void loginSucceeded(Person person) {
        loginAttempsRepository.findByPerson(person)
            .ifPresent(loginAttempsRepository::delete);
    }

    @Override
    // Registra un intento fallido de inicio de sesión
    public void loginFailed(Person person) {
        LoginAttempt loginAttempt = loginAttempsRepository.findByPerson(person)
             .orElseGet(() -> {
                LoginAttempt newAttempt = new LoginAttempt();
                newAttempt.setPerson(person);
                newAttempt.setAttempts(0);
                return newAttempt;
             });

        // Si actualmente está bloqueado, lanzar excepción con detalles
        if (loginAttempt.getBlockUntil() != null &&
            loginAttempt.getBlockUntil().toLocalDateTime().isAfter(LocalDateTime.now())) {
            Instant until = loginAttempt.getBlockUntil().toInstant();
            boolean permanent = until.isAfter(Instant.now().plusSeconds(60L * 60L * 24L * 365L * 50L)); // ~50 años
            throw new AccountBlockedException("Usuario bloqueado", until, permanent);
        }

        // Registrar intento fallido
        int prevAttempts = loginAttempt.getAttempts() == null ? 0 : loginAttempt.getAttempts();
        int newAttempts = prevAttempts + 1;
        loginAttempt.setAttempts(newAttempts);
        loginAttempt.setLastAttempt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        // Umbral fase 1: 5 intentos → bloquear 15 minutos
        if (newAttempts == MAX_FIRST_ATTEMPT) {
            LocalDateTime until = LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES);
            loginAttempt.setBlockUntil(java.sql.Timestamp.valueOf(until));
            loginAttempsRepository.save(loginAttempt);
            throw new AccountBlockedException("Cuenta bloqueada temporalmente", until.toInstant(ZoneOffset.UTC), false);
        }

        // Umbral fase 2 (permanente): 5 + 2 intentos
        if (newAttempts >= MAX_FIRST_ATTEMPT + MAX_SECOND_ATTEMPT) {
            LocalDateTime until = LocalDateTime.now().plusYears(100);
            loginAttempt.setBlockUntil(java.sql.Timestamp.valueOf(until));
            loginAttempsRepository.save(loginAttempt);
            throw new AccountBlockedException("Cuenta bloqueada permanentemente", until.toInstant(ZoneOffset.UTC), true);
        }

        // Aún no bloqueado: persistir y lanzar InvalidCredentials con intentos restantes
        loginAttempsRepository.save(loginAttempt);

        int remaining;
        if (newAttempts < MAX_FIRST_ATTEMPT) {
            remaining = MAX_FIRST_ATTEMPT - newAttempts;
        } else {
            // Fase 2 tras desbloqueo (cuando ocurra). Si llegamos aquí sin bloquear, usamos margen de fase 2
            remaining = MAX_SECOND_ATTEMPT - (newAttempts - MAX_FIRST_ATTEMPT);
        }

        throw new InvalidCredentialsException("Credenciales inválidas", Math.max(remaining, 0));
    }

    @Override
    // Verifica si un usuario está bloqueado debido a múltiples intentos fallidos
    public boolean isBlocked(Person person) {
        return loginAttempsRepository.findByPerson(person)
        .filter(attempt -> attempt.getBlockUntil() != null &&
        attempt.getBlockUntil().toLocalDateTime().isAfter(LocalDateTime.now()))
        .isPresent();
    }

    @Override
    public void checkBlocked(Person person) {
        loginAttempsRepository.findByPerson(person).ifPresent(attempt -> {
            if (attempt.getBlockUntil() != null && attempt.getBlockUntil().toLocalDateTime().isAfter(LocalDateTime.now())) {
                Instant until = attempt.getBlockUntil().toInstant();
                boolean permanent = until.isAfter(Instant.now().plusSeconds(60L * 60L * 24L * 365L * 50L));
                throw new AccountBlockedException("Usuario bloqueado", until, permanent);
            }
        });
    }
}
