package com.innosistemas.authenticator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthErrorResponse {
    private String message;
    private String code; // e.g., AUTH_INVALID_CREDENTIALS, AUTH_BLOCKED_TEMPORARY, AUTH_BLOCKED_PERMANENT
    private Integer remainingAttempts; // nullable
    private String blockedUntil; // ISO-8601, nullable
    private Long remainingMillis; // nullable
    private Boolean permanent; // nullable
}
