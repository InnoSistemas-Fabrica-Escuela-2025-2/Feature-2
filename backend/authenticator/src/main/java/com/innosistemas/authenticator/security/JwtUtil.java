package com.innosistemas.authenticator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") 
    private String secretKey;

    @Value("${jwt.expiration}") 
    private Long jwtExpiration;

    // Obtener la clave de firma a partir del secreto
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Extraer el correo electrónico del token JWT
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer la fecha de expiración del token JWT
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer el rol del token JWT
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extraer el ID del usuario del token JWT
    public Long extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    // Extraer cualquier calim del token JWT usando una función de resolución de reclamos
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer todos los claims del token JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Verificar si el token JWT ha expirado   
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generar un token JWT con ID de usuario, correo electrónico y rol
    public String generateToken(Long id_usuario, String email, String role) {
        Map<String, Object> claims = buildClaims(id_usuario, role);
        return createToken(claims, email);
    }

    // Crear el token JWT con los claims y el sujeto proporcionados
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())    // Firmar el token con la clave de firma definida
                .compact();  
    }

    // Validar el token JWT comparando el correo electrónico extraído y verificando la expiración
    public Boolean validateToken(String token, String email) {
        return validateToken(token, email, true);
    }

    // Validar el token JWT verificando la expiración
    public Boolean validateToken(String token) {
        return validateToken(token, null, false);
    }

    private Boolean validateToken(String token, String email, boolean requireSubjectMatch) {
        if (!isTokenSignatureAndDateValid(token)) {
            return false;
        }

        if (!requireSubjectMatch) {
            return true;
        }

        return isSubjectValid(extractEmail(token), email);
    }

    private boolean isTokenSignatureAndDateValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSubjectValid(String extracted, String expected) {
        return expected != null && expected.equals(extracted);
    }

    private Map<String, Object> buildClaims(Long id, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("id", id);
        return claims;
    }
}
