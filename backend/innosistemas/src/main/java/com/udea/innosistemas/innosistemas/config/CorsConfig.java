package com.udea.innosistemas.innosistemas.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:5173}")
    private String allowedOrigins;

    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales (cookies, headers de autenticación)
        config.setAllowCredentials(true);
        
        // Permitir peticiones desde el frontend (desarrollo y producción)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, etc.)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Aplicar configuración a todas las rutas del backend
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
