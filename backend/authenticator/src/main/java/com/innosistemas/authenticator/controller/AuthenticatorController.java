package com.innosistemas.authenticator.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.innosistemas.authenticator.dto.AuthenticatorRequest;
import com.innosistemas.authenticator.dto.AuthenticatorResponse;
import com.innosistemas.authenticator.service.AuthenticatorService;
import com.innosistemas.authenticator.service.ActiveSessionService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/person")
public class AuthenticatorController {
    
    @Autowired
    private AuthenticatorService authenticatorService;

    @Autowired
    private ActiveSessionService activeSessionService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatorResponse> login(@RequestBody AuthenticatorRequest request, HttpServletRequest httpRequest) {
        AuthenticatorResponse response = authenticatorService.login(request);

        ResponseCookie cookie = buildJwtCookie(response.getToken(), httpRequest.isSecure());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        String token = resolveTokenFromRequest(httpRequest);
        if (token != null) {
            activeSessionService.invalidateSessionByToken(token);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("access_token", "")
            .maxAge(Duration.ZERO)
            .path("/")
            .httpOnly(true)
            .secure(httpRequest.isSecure())
            .sameSite("Lax")
            .build();

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
            .build();
    }

    @GetMapping("/message")
    public ResponseEntity<String> showMesagge() {
        return ResponseEntity.ok("servicio 1 funcionando");
    }

    private ResponseCookie buildJwtCookie(String token, boolean secure) {
        return ResponseCookie.from("access_token", token)
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .maxAge(Duration.ofMillis(jwtExpiration))
            .sameSite("Lax")
            .build();
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
    
}
