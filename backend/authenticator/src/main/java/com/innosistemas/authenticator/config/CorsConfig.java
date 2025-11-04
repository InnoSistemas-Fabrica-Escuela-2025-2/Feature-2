package com.innosistemas.authenticator.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// DESACTIVADO: El Gateway ya maneja CORS
// @Configuration
public class CorsConfig {

    // @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales (cookies, headers de autenticación)
        config.setAllowCredentials(true);
        
        // Permitir peticiones desde el frontend (puerto 5173 es el default de Vite)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080"));
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, etc.)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Aplicar configuración a todas las rutas del backend
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
