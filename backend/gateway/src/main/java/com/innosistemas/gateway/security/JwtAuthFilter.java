package com.innosistemas.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.lang.NonNull;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class JwtAuthFilter implements WebFilter{
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authheader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authheader == null || !authheader.startsWith("Bearer ")){
            log.debug("JWT: Authorization header ausente o sin prefijo Bearer; permitiendo paso sin autenticación");
            return chain.filter(exchange);
        }

        String token = authheader.substring(7);

        try {
            var key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String role = String.valueOf(claims.get("role"));
            String normalizedRole = role == null ? "" : role.trim().toLowerCase();
            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(normalizedRole));

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

            log.debug("JWT: token válido. subject={}, role={} (normalized={})", claims.getSubject(), role, normalizedRole);

            ServerWebExchange mutatedExchange = new ServerWebExchangeDecorator(exchange) {
                @Override
                public @NonNull org.springframework.http.server.reactive.ServerHttpRequest getRequest() {
                    return exchange.getRequest().mutate()
                            .header("Email", claims.getSubject())
                            .header("Role", normalizedRole)
                            .build();
                }
            };

            return chain
                .filter(mutatedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (Exception e) {
            log.error("JWT: validación fallida: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }; 
}
