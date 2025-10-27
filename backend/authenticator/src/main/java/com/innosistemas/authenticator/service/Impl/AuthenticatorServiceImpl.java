package com.innosistemas.authenticator.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.PersonRepository;
import com.innosistemas.authenticator.security.JwtUtil;
import com.innosistemas.authenticator.service.AuthenticatorService;

@Service
public class AuthenticatorServiceImpl implements AuthenticatorService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public AuthenticatorResponse login(AuthenticatorRequest request) {
    Optional<Person> present = personRepository.findByEmail(request.getEmail());
        if (present.isPresent()){
            Person person = present.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordValid = encoder.matches(request.getPassword(), person.getPassword());
            if (!isPasswordValid) {
                throw new RuntimeException("Contraseña incorrecta");
            }

            String token = jwtUtil.generateToken(person.getId(), 
            person.getEmail(), 
            person.getRole());

            AuthenticatorResponse response = new AuthenticatorResponse(token, 
            person.getEmail(),
            person.getRole());

            return response;
        } else {
            throw new UnsupportedOperationException ("Correo no encontrado");
        }
    }
}
