package com.innosistemas.authenticator.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;
import com.innosistemas.authenticator.repository.PersonRepository;
import com.innosistemas.authenticator.entity.Person;


public class AuthenticatorService implements IAuthenticatorService {

    @Autowired
    private PersonRepository personRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public AuthenticatorResponse login(AuthenticatorRequest request) {
    
        Person person = personRepository.findByCorreoElectronico(request.getEmail())
        .orElseThrow(() -> new RuntimeException("Correo no encontrado"));

        boolean isPasswordValid = BCrypt.checkpw(request.getPassword(), person.getPassword());
        if (!isPasswordValid) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        String token = generateToken(person);
        
        AuthenticatorResponse response = new AuthenticatorResponse();
        response.setEmail(person.getEmail());
        response.setToken(token);
        return response;
    }
    

    private String generateToken(Person person) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", person.getRole());
        claims.put("nombre", person.getUsername());
        claims.put("id", person.getId());

        return Jwts.builder()
                .setSubject(person.getEmail())
                .addClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) 
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
