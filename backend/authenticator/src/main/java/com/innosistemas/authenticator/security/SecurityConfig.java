package com.innosistemas.authenticator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())  // Deshabilitar CORS de Spring Security (usamos CorsConfig)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                    // Rutas del authenticator (después de StripPrefix) - PÚBLICAS
                    .requestMatchers("/person/authenticate", "/person/message", "/person/**").permitAll()
                    // Rutas de proyectos (si se enrutan a través de este servicio)
                    .requestMatchers("/project/project/**").hasRole("STUDENT")
                    .requestMatchers("/project/project/listAll").hasRole("PROFESOR")
                    .requestMatchers("/project/objective/**").hasRole("STUDENT")
                    .requestMatchers("/project/task/**").hasRole("STUDENT")
                    .requestMatchers("/project/state/**").hasRole("STUDENT")
                    .anyRequest().permitAll()  // Permitir todo temporalmente para debug
                    )
                    
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }

}
