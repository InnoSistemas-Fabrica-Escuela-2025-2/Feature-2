package com.innosistemas.authenticator.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;
import com.innosistemas.authenticator.entity.Person;
import com.innosistemas.authenticator.repository.PersonRepository;
import com.innosistemas.authenticator.service.AuthenticatorService;

@Service
public class AuthenticatorServiceImpl implements AuthenticatorService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public AuthenticatorResponse login(AuthenticatorRequest request) {
        System.out.println("hola");
    Optional<Person> present = personRepository.findByEmail(request.getEmail());
        if (present.isPresent()){
            Person person = present.get();
            boolean isPasswordValid = BCrypt.checkpw(request.getPassword(), person.getPassword());
            if (!isPasswordValid) {
                throw new RuntimeException("Contraseña incorrecta");
            }

            AuthenticatorResponse response = new AuthenticatorResponse();
            response.setEmail(person.getEmail());

            return response;

        } else {
            throw new UnsupportedOperationException ("Correo no encontrado");
        }
    }
}
