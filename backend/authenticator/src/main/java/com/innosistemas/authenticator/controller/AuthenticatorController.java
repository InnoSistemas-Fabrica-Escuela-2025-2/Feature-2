package com.innosistemas.authenticator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private AuthenticatorService authenticatorService;
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatorResponse> login(@RequestBody AuthenticatorRequest request) {
        try {
            AuthenticatorResponse response = authenticatorService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
