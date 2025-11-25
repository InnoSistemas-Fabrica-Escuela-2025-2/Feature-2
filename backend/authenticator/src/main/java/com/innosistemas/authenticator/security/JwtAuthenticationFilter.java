package com.innosistemas.authenticator.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    // Utilidad para manejar JWT
    private JwtUtil jwtUtil;

    @Override
    /*  Filtro que se ejecuta una vez por solicitud para autenticar el JWT
     chain permite continuar con la cadena de filtros */
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip JWT validation for actuator endpoints
        if (path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Extraer el correo electrónico del token JWT
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                // Invalid token
            }
        }


        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Validar que el token JWT es válido
            if (jwtUtil.validateToken(jwt)) {
                String role = jwtUtil.extractRole(jwt);

                // Crea una lista de roles autorizados basados en el rol extraído del token
                List<SimpleGrantedAuthority> authorities;
                if (role != null && role.equals("profesor")) {
                    authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_PROFESOR"));
                } else {
                    authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
                }

                // Crear un token de autenticación con el correo electrónico y los roles
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null,
                        authorities);

                // Establecer detalles de la solicitud en el token de autenticación
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

}
