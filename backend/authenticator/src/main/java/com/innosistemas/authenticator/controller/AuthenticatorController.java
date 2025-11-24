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

import io.swagger.v3.oas.annotations.Operation;



@RestController
@RequestMapping("/authenticator/person")
public class AuthenticatorController {
    
    @Autowired
    // Servicio de autenticación
    private AuthenticatorService authenticatorService;
    
    @Operation(
    summary = "Autenticar un usuario y generar un token"
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatorResponse> login(@RequestBody AuthenticatorRequest request) {
        try {
            AuthenticatorResponse response = authenticatorService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
    summary = "Mostrar mensaje de estado del servicio"
    )
    @GetMapping("/message")
    public ResponseEntity<String> showMesagge() {
        return ResponseEntity.ok("servicio 1 funcionando");
    }
    
}
