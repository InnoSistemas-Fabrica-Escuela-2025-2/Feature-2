package com.udea.innosistemas.innosistemas.config;

<<<<<<< HEAD
import java.util.Arrays;

=======
import org.springframework.beans.factory.annotation.Value;
>>>>>>> origin/frontend-actualizado
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

<<<<<<< HEAD
=======
import java.util.Arrays;
import java.util.List;

>>>>>>> origin/frontend-actualizado
@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales (cookies, headers de autenticación)
        config.setAllowCredentials(true);
        
<<<<<<< HEAD
        // Permitir peticiones desde el frontend (puerto 5173 es el default de Vite)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080"));
=======
        // Permitir peticiones desde el frontend (desarrollo y producción)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOrigins(origins);
>>>>>>> origin/frontend-actualizado
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, etc.)
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Aplicar configuración a todas las rutas del backend
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
