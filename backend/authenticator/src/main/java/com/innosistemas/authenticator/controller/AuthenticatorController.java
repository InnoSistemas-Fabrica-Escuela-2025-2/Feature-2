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
    
    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    private final AuthenticatorService authenticatorService;

    private final ActiveSessionService activeSessionService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    public AuthenticatorController(AuthenticatorService authenticatorService, ActiveSessionService activeSessionService) {
        this.authenticatorService = authenticatorService;
        this.activeSessionService = activeSessionService;
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticatorResponse> login(@RequestBody AuthenticatorRequest request, HttpServletRequest httpRequest) {
        AuthenticatorResponse response = authenticatorService.login(request);
        ResponseCookie cookie = createJwtCookie(response.getToken(), httpRequest.isSecure());
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        invalidateSessionIfTokenPresent(httpRequest);
        ResponseCookie deleteCookie = createDeleteCookie(httpRequest.isSecure());
        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
            .build();
    }

    @GetMapping("/message")
    public ResponseEntity<String> showMessage() {
        return ResponseEntity.ok("servicio 1 funcionando");
    }

    private ResponseCookie createJwtCookie(String token, boolean secure) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
            .httpOnly(true)
            .secure(secure)
            .path("/")
            .maxAge(Duration.ofMillis(jwtExpiration))
            .sameSite("Lax")
            .build();
    }

    private ResponseCookie createDeleteCookie(boolean secure) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
            .maxAge(Duration.ZERO)
            .path("/")
            .httpOnly(true)
            .secure(secure)
            .sameSite("Lax")
            .build();
    }

    private void invalidateSessionIfTokenPresent(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            activeSessionService.invalidateSessionByToken(token);
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
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
