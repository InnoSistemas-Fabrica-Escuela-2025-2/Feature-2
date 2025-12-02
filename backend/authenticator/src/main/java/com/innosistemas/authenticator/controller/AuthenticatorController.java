package com.innosistemas.authenticator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;
import com.innosistemas.authenticator.service.AuthenticatorService;



@RestController
@RequestMapping("/person")
public class AuthenticatorController {
    
    @Autowired
    // Servicio de autenticación
    private AuthenticatorService authenticatorService;
    
    @PostMapping("/authenticate")
    // Endpoint para autenticar usuarios
    public ResponseEntity<AuthenticatorResponse> login(@RequestBody AuthenticatorRequest request) {
        AuthenticatorResponse response = authenticatorService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/message")
    // Endpoint de prueba para verificar que el servicio está funcionando
    public ResponseEntity<String> showMesagge() {
        return ResponseEntity.ok("servicio 1 funcionando");
    }
    
}
