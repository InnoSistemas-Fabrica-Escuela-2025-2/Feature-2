package com.innosistemas.authenticator.service.Impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ActiveSessionRepository activeSessionRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private ActiveSessionService activeSessionService;

    @Override
    public AuthenticatorResponse login(AuthenticatorRequest request) {
    Optional<Person> present = personRepository.findByEmail(request.getEmail());

        if (present.isPresent()){
            Person person = present.get();

            if (loginAttemptService.isBlocked(person)){
                System.out.println("Usuario bloqueado por múltiples intentos fallidos: " + person.getEmail());
                throw new RuntimeException("Usuario bloqueado por múltiples intentos fallidos");
            }

            Optional<ActiveSession> session = activeSessionRepository.findByPerson(person);
            if (session.isPresent()) {
                ActiveSession existingSession = session.get();
                System.out.println("Verificando sesión activa para: " + person.getEmail());
                
                if (jwtUtil.validateToken(existingSession.getToken())) {
                    System.out.println("El usuario ya tiene una sesión activa y válida: " + person.getEmail());
                    throw new RuntimeException("El usuario ya tiene una sesión activa.");
                } else {
                    System.out.println("Token expirado, se eliminará la sesión anterior: " + person.getEmail());
                    activeSessionService.invalidateSession(existingSession);
                    activeSessionRepository.flush(); 
                }
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordValid = encoder.matches(request.getPassword(), person.getPassword());
            
            if (!isPasswordValid) {
                loginAttemptService.loginFailed(person);
                throw new RuntimeException("Contraseña incorrecta");
            }

            loginAttemptService.loginSucceeded(person);

            String token = jwtUtil.generateToken(person.getId(), 
            person.getEmail(), 
            person.getRole());

            activeSessionService.registerSession(person, token);

            AuthenticatorResponse response = new AuthenticatorResponse(token, 
            person.getEmail(),
            person.getRole());

            return response;
        } else {
            throw new UnsupportedOperationException ("Correo no encontrado");
        }
    }
}
