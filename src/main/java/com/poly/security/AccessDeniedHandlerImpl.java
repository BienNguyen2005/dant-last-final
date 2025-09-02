package com.poly.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.debug("AccessDenied: response already committed for path {}", request.getRequestURI());
            return;
        }

        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        String xhr = request.getHeader("X-Requested-With");
        boolean jsonPreferred = accept != null && accept.contains("application/json");
        boolean ajax = xhr != null && xhr.equalsIgnoreCase("XMLHttpRequest");
        boolean fragment = uri.startsWith("/fragment/");
        boolean apiLike = uri.startsWith("/api/") || uri.startsWith("/cart") || uri.startsWith("/cartItem");

        boolean wantsJson = jsonPreferred || ajax || fragment || apiLike;

        log.debug("AccessDenied for uri={}, wantsJson={} (accept={}, xhr={}, fragment={}, apiLike={})", uri, wantsJson,
                accept, xhr, fragment, apiLike);

        String dispatcherType = request.getDispatcherType().name();
        if ("INCLUDE".equals(dispatcherType) || "FORWARD".equals(dispatcherType)) {
            log.trace("AccessDenied during dispatcher type {} for {} -> sending 403 error", dispatcherType, uri);
            response.resetBuffer();
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (wantsJson) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter()
                    .write("{\"error\":\"Forbidden\",\"message\":\"Bạn không có quyền truy cập tài nguyên này\"}");
        } else {
            if (uri.equals("/access-denied")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.sendRedirect("/access-denied");
            }
        }
    }
}
