package com.innosistemas.gateway.config;
 
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    // Configuración de CORS para permitir solicitudes desde el frontend
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();  // Crear una nueva configuración de CORS

        config.setAllowCredentials(true); // Permitir el envío de cookies y credenciales
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Orígenes permitidos
        config.addAllowedHeader("*");   // Permitir todos los encabezados
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));    // Métodos HTTP permitidos

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplicar la configuración a todas las rutas

        return new CorsWebFilter(source);
    }
}