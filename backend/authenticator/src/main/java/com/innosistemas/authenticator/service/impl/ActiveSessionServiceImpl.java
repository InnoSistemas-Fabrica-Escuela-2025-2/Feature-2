package com.innosistemas.authenticator.service.impl;

import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.innosistemas.authenticator.entity.ActiveSession;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.ActiveSessionRepository;
import com.innosistemas.authenticator.security.JwtUtil;
import com.innosistemas.authenticator.service.ActiveSessionService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ActiveSessionServiceImpl implements ActiveSessionService {

    private static final Logger logger = LoggerFactory.getLogger(ActiveSessionServiceImpl.class);

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final JwtUtil jwtUtil;
    private final ActiveSessionRepository activeSessionRepository;

    public ActiveSessionServiceImpl(JwtUtil jwtUtil, ActiveSessionRepository activeSessionRepository) {
        this.jwtUtil = jwtUtil;
        this.activeSessionRepository = activeSessionRepository;
    }

    @Override
    @Transactional
    public synchronized void registerSession(Person person, String token) {
        activeSessionRepository.findByPerson(person)
            .ifPresent(activeSessionRepository::delete);

        ActiveSession activeSession = new ActiveSession();
        activeSession.setToken(token);
        activeSession.setPerson(person);
        activeSession.setExpiresAt(Timestamp.from(
            java.time.Instant.now().plusMillis(jwtExpiration)));
        activeSessionRepository.save(activeSession);
    }

    @Override
    public void invalidateSession(ActiveSession session) {
        if (session == null) {
            throw new IllegalStateException("ActiveSession must not be null");
        }
        activeSessionRepository.delete(session);
    }

    @Override
    @Transactional
    public void invalidateSessionByToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        activeSessionRepository.findByToken(token)
            .ifPresent(activeSessionRepository::delete);
    }

    @Override
    public boolean isSessionActive(Person person) {
        if (person == null) {
            return false;
        }

        return activeSessionRepository.findByPerson(person)
            .map(session -> {
                String token = session.getToken();
                if (token == null || token.isBlank()) {
                    return false;
                }
                // jwtUtil.isTokenExpired may return boolean or Boolean; handle null-safely
                Boolean expired = jwtUtil.isTokenExpired(token);
                if (Boolean.TRUE.equals(expired)) {
                    logger.info("Token expirado para usuario: {}", person.getEmail());
                    activeSessionRepository.delete(session);
                    return false;
                }
                return true;
            })
            .orElse(false);
    }
}