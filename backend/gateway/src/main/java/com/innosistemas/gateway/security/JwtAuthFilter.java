package com.innosistemas.gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private String extractToken(ServerHttpRequest request) {
        String authheader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authheader != null && authheader.startsWith("Bearer ")) {
            return authheader.substring(7);
        }
        HttpCookie cookie = request.getCookies().getFirst("access_token");
        if (cookie != null && !cookie.getValue().isBlank()) {
            return cookie.getValue();
        }
        return null;
    }

    @Override
    public @NonNull
    Mono<Void> filter(@NonNull ServerWebExchange exchange,
            @NonNull WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String token = extractToken(request);

        if (token == null || token.isBlank()) {
            return chain.filter(exchange);
        }

        Claims claims = validateToken(token);
        if (claims == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String role = String.valueOf(claims.get("role"));
        String userId = String.valueOf(claims.get("id"));
        String normalizedRole = role == null ? "" : role.trim().toLowerCase();

        Collection<GrantedAuthority> authorities
                = List.of(new SimpleGrantedAuthority(normalizedRole));

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, authorities);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Email", claims.getSubject())
                .header("Role", normalizedRole)
                .header("User-Id", userId)
                .build();

        ServerWebExchange mutatedExchange
                = exchange.mutate().request(mutatedRequest).build();
        
                
        return chain.filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private Claims validateToken(String token) {
        try {
            var key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.ExpiredJwtException | IllegalArgumentException e) {
            log.error("JWT: validación fallida: {}", e.getMessage());
            return Jwts.claims().build();
        }
    }
}
