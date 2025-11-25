package com.innosistemas.authenticator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String role = "STUDENT";

    @Autowired
    // Filtro de autenticación JWT
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    // Configuración de la cadena de filtros de seguridad
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())  // Disable CORS - handled by Gateway
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow CORS preflight
                    .requestMatchers("/person/**").permitAll()  // Authenticator endpoints (after StripPrefix)
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated()
                    )
            // Agregar el filtro de autenticación JWT antes del filtro de nombre de usuario y contraseña
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }

}
