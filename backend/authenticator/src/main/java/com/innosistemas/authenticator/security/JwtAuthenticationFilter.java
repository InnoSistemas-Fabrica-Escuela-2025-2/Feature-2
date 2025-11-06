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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        String jwt = resolveToken(request);
        if (jwt == null) {
            chain.doFilter(request, response);
            return;
        }

        String email = extractEmailSafely(jwt);
        if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.validateToken(jwt, email)) {
            chain.doFilter(request, response);
            return;
        }

        List<SimpleGrantedAuthority> authorities = buildAuthorities(jwtUtil.extractRole(jwt));
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String extractEmailSafely(String jwt) {
        try {
            return jwtUtil.extractEmail(jwt);
        } catch (Exception e) {
            return null;
        }
    }

    private List<SimpleGrantedAuthority> buildAuthorities(String role) {
        if (role != null && role.equalsIgnoreCase("profesor")) {
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESOR"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }
}
