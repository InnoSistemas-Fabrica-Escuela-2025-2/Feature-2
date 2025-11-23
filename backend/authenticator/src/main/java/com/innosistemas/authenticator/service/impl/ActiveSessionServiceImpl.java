package com.innosistemas.authenticator.service.Impl;

import java.sql.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.innosistemas.authenticator.entity.ActiveSession;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.ActiveSessionRepository;
import com.innosistemas.authenticator.security.JwtUtil;
import com.innosistemas.authenticator.service.ActiveSessionService;
import jakarta.transaction.Transactional;

@Service
public class ActiveSessionServiceImpl implements ActiveSessionService {

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ActiveSessionRepository activeSessionRepository;

    @Override
    @Transactional
    // Guardar la sesión activa de un usuario
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
    // Eliminar la sesión de un usuario
    public void invalidateSession(ActiveSession session) {
        activeSessionRepository.delete(session);
    }

    @Override
    // Verificar si la sesión de un usuario está activa por medio de la expiración del token
    public boolean isSessionActive(Person person) {
        return activeSessionRepository.findByPerson(person)
            .map(session -> {
                String token = session.getToken();
                if (jwtUtil.isTokenExpired(token)) {
                    System.out.println("Token expirado para usuario: " + person.getEmail());
                    return false;
                }
                return true; 
            })
            .orElse(false);
    }
}