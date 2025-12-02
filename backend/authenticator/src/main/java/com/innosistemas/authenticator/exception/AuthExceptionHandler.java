package com.innosistemas.authenticator.exception;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.innosistemas.authenticator.dto.AuthErrorResponse;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        AuthErrorResponse body = AuthErrorResponse.builder()
            .message("Correo o contraseña incorrectos.")
            .code("AUTH_INVALID_CREDENTIALS")
            .remainingAttempts(ex.getRemainingAttempts())
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<AuthErrorResponse> handleAccountBlocked(AccountBlockedException ex) {
        Instant until = ex.getBlockedUntil();
        Long remainingMillis = null;
        String blockedUntilIso = null;
        if (until != null) {
            long ms = Math.max(0, until.toEpochMilli() - Instant.now().toEpochMilli());
            remainingMillis = ms;
            blockedUntilIso = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(until);
        }
        String code = ex.isPermanent() ? "AUTH_BLOCKED_PERMANENT" : "AUTH_BLOCKED_TEMPORARY";
        String message = ex.isPermanent() ? "Tu cuenta ha sido bloqueada permanentemente." : "Tu cuenta está bloqueada temporalmente.";
        AuthErrorResponse body = AuthErrorResponse.builder()
            .message(message)
            .code(code)
            .blockedUntil(blockedUntilIso)
            .remainingMillis(remainingMillis)
            .permanent(ex.isPermanent())
            .build();
        return ResponseEntity.status(423).body(body);
    }
}
