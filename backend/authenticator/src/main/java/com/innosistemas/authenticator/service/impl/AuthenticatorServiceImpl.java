package com.innosistemas.authenticator.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;
import com.innosistemas.authenticator.entity.ActiveSession;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.ActiveSessionRepository;
import com.innosistemas.authenticator.repository.PersonRepository;
import com.innosistemas.authenticator.security.JwtUtil;
import com.innosistemas.authenticator.service.ActiveSessionService;
import com.innosistemas.authenticator.service.AuthenticatorService;
import com.innosistemas.authenticator.service.LoginAttemptService;

@Service
public class AuthenticatorServiceImpl implements AuthenticatorService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticatorServiceImpl.class);

    private final JwtUtil jwtUtil;
    private final PersonRepository personRepository;
    private final ActiveSessionRepository activeSessionRepository;
    private final LoginAttemptService loginAttemptService;
    private final ActiveSessionService activeSessionService;

    @Autowired
    public AuthenticatorServiceImpl(
            JwtUtil jwtUtil,
            PersonRepository personRepository,
            ActiveSessionRepository activeSessionRepository,
            LoginAttemptService loginAttemptService,
            ActiveSessionService activeSessionService) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
        this.activeSessionRepository = activeSessionRepository;
        this.loginAttemptService = loginAttemptService;
        this.activeSessionService = activeSessionService;
    }

    @Override
    public AuthenticatorResponse login(AuthenticatorRequest request) {
    Optional<Person> present = personRepository.findByEmail(request.getEmail());

        if (present.isPresent()){
            Person person = present.get();

            if (loginAttemptService.isBlocked(person)){
                logger.warn("Usuario bloqueado por múltiples intentos fallidos: {}", person.getEmail());
                throw new ResponseStatusException(HttpStatus.LOCKED, "Usuario bloqueado por múltiples intentos fallidos");
            }

            Optional<ActiveSession> session = activeSessionRepository.findByPerson(person);
            if (session.isPresent()) {
                ActiveSession existingSession = session.get();
                logger.info("Verificando sesión activa para: {}", person.getEmail());
                
                // Siempre invalidar la sesión anterior y crear una nueva
                logger.info("Invalidando sesión anterior y creando nueva: {}", person.getEmail());
                activeSessionService.invalidateSession(existingSession);
                activeSessionRepository.flush();
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordValid = encoder.matches(request.getPassword(), person.getPassword());
            
            if (!isPasswordValid) {
                loginAttemptService.loginFailed(person);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
            }

            loginAttemptService.loginSucceeded(person);

            String token = jwtUtil.generateToken(person.getId(), 
            person.getEmail(), 
            person.getRole());

            activeSessionService.registerSession(person, token);

            return new AuthenticatorResponse(
                person.getId(),
                token,
                person.getEmail(),
                person.getRole()
            ) ;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No encontramos una cuenta registrada con ese correo.");
        }
    }
}
