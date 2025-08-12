package com.poly.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // For API requests, return 403 Forbidden
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
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Bạn không có quyền truy cập tài nguyên này\"}");
        } else {
            response.sendRedirect("/access-denied");
        }
    }
}
