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
    // Manejar el proceso de inicio de sesión
    public AuthenticatorResponse login(AuthenticatorRequest request) {
    Optional<Person> present = personRepository.findByEmail(request.getEmail());

        // Si el usuario existe, proceder con la autenticación
        if (present.isPresent()){
            Person person = present.get();

            // Verificar si el usuario está bloqueado por múltiples intentos fallidos
            if (loginAttemptService.isBlocked(person)){
                System.out.println("Usuario bloqueado por múltiples intentos fallidos: " + person.getEmail());
                throw new RuntimeException("Usuario bloqueado por múltiples intentos fallidos");
            }

            // Verificar si ya existe una sesión activa para el usuario
            Optional<ActiveSession> session = activeSessionRepository.findByPerson(person);
            if (session.isPresent()) {
                ActiveSession existingSession = session.get();
                System.out.println("Verificando sesión activa para: " + person.getEmail());
                
                // Validar el token de la sesión existente
                if (jwtUtil.validateToken(existingSession.getToken())) {
                    System.out.println("El usuario ya tiene una sesión activa y válida: " + person.getEmail());
                    throw new RuntimeException("El usuario ya tiene una sesión activa.");
                } else {
                    System.out.println("Token expirado, se eliminará la sesión anterior: " + person.getEmail());
                    activeSessionService.invalidateSession(existingSession);
                    activeSessionRepository.flush();  // Asegurar que la sesión se elimine antes de continuar
                }
            }

            // Validar la contraseña proporcionada con la almacenada
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordValid = encoder.matches(request.getPassword(), person.getPassword());
            
            if (!isPasswordValid) {
                loginAttemptService.loginFailed(person);
                throw new RuntimeException("Contraseña incorrecta");
            }

            // Registro de un inicio de sesión exitoso
            loginAttemptService.loginSucceeded(person);

            String token = jwtUtil.generateToken(person.getId(), 
            person.getEmail(), 
            person.getRole());

            // Registrar la nueva sesión activa
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
