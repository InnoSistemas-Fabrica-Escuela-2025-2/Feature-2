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


@Component
public class JwtAuthFilter implements WebFilter{
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authheader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authheader == null || !authheader.startsWith("Bearer ")){
            return chain.filter(exchange);
        }

        String token = authheader.substring(7);

        try {
            Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            ServerWebExchange mutatedExchange = new ServerWebExchangeDecorator(exchange) {
                @Override
                public org.springframework.http.server.reactive.ServerHttpRequest getRequest() {
                    return exchange.getRequest().mutate()
                            .header("Email", claims.getSubject())
                            .header("Role", (String) claims.get("role"))
                            .build();
                }
            };

            return chain.filter(mutatedExchange);
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }; 
}
