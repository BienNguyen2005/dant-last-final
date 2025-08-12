package com.poly.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        
        // For API requests, return 401 Unauthorized
        boolean isApi = false;
        String accept = request.getHeader("Accept");
        String xhr = request.getHeader("X-Requested-With");
        String path = request.getServletPath();
        if ((accept != null && accept.contains("application/json")) ||
            (xhr != null && xhr.equalsIgnoreCase("XMLHttpRequest")) ||
            path.startsWith("/cart") || path.startsWith("/cartItem")) {
            isApi = true;
        }
        if (isApi) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
        } else {
            // For browser requests, redirect to login page
            response.sendRedirect("/signin?error=unauthorized");
        }
    }
}
