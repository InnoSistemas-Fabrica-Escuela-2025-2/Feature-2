package com.innosistemas.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String role = "estudiante";
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter){
        this.jwtAuthFilter = jwtAuthFilter;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow CORS preflight
                .pathMatchers("/authenticator/**").permitAll()  // Allow all authenticator endpoints
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/project/project/listAll").hasAuthority("profesor")
                .pathMatchers("/project/project/**").hasAuthority(role)
                .pathMatchers("/project/objective/**").hasAuthority(role)
                .pathMatchers("/project/task/**").hasAuthority(role)
                .pathMatchers("/project/state/**").hasAuthority(role)
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}