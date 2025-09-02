package com.poly.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.debug("AuthEntryPoint: response already committed for {}", request.getRequestURI());
            return; // Avoid writing after response committed
        }
        
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
            log.trace("AuthEntryPoint 401 JSON for path {} (Accept={}, XHR={})", path, accept, xhr);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}");
        } else {
            // For browser requests, redirect to login page
            log.trace("AuthEntryPoint redirect to /signin for path {}", path);
            response.sendRedirect("/signin?error=unauthorized");
        }
    }
}
