package com.innosistemas.authenticator.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private static final String SECRET_KEY = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";
    private static final long ONE_HOUR_IN_MILLIS = 3_600_000L;
    private static final String DEFAULT_EMAIL = "user@example.com";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        setField("secretKey", SECRET_KEY);
        setField("jwtExpiration", ONE_HOUR_IN_MILLIS);
    }

    @Test
    void generateTokenPopulatesMandatoryClaims() {
        Long userId = 42L;
        String email = DEFAULT_EMAIL;
        String role = "ADMIN";

        String token = jwtUtil.generateToken(userId, email, role);

        assertEquals(email, jwtUtil.extractEmail(token));
        assertEquals(role, jwtUtil.extractRole(token));
        assertEquals(userId, jwtUtil.extractId(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void validateTokenWithMatchingEmailAndValidExpirationReturnsTrue() {
        String email = DEFAULT_EMAIL;
        String token = jwtUtil.generateToken(99L, email, "USER");

        assertTrue(jwtUtil.validateToken(token, email));
    }

    @Test
    void validateTokenWithDifferentEmailReturnsFalse() {
        String token = jwtUtil.generateToken(99L, DEFAULT_EMAIL, "USER");

        assertFalse(jwtUtil.validateToken(token, "other@example.com"));
    }

    @Test
    void validateTokenWithExpiredTokenReturnsFalse() {
        setField("jwtExpiration", -1_000L);
        String token = jwtUtil.generateToken(99L, "expired@example.com", "USER");

        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void validateTokenWithMalformedTokenReturnsFalse() {
        assertFalse(jwtUtil.validateToken("not-a-valid-token"));
    }

    private void setField(String fieldName, Object value) {
        try {
            Field field = JwtUtil.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(jwtUtil, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field via reflection", e);
        }
    }
}
