package com.innosistemas.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/authenticator/person/authenticate", "/authenticator/person/message").permitAll()
                .pathMatchers("/project/project/listAll").hasAuthority("profesor")
                .pathMatchers("/project/project/**").hasAuthority("estudiante")
                .pathMatchers("/project/objective/**").hasAuthority("estudiante")
                .pathMatchers("/project/task/**").hasAuthority("estudiante")
                .pathMatchers("/project/state/**").hasAuthority("estudiante")
                .anyExchange().authenticated()
            )
            .build();
    }
}