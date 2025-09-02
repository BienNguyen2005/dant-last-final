package com.poly.filter;

import java.io.IOException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import jakarta.servlet.http.HttpSession;

/**
 * Temporary diagnostics filter: logs current principal, authorities, and
 * session currentUser id.
 * Remove or disable after debugging.
 */
@Component
public class SecurityDiagnosticsFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SecurityDiagnosticsFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        // Skip noisy static resources
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/img/") || uri.startsWith("/image/")) {
            filterChain.doFilter(request, response);
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth != null ? String.valueOf(auth.getPrincipal()) : "null";
        String authorities = auth != null && auth.getAuthorities() != null
                ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))
                : "";
        HttpSession session = request.getSession(false);
        Object sessionUser = null;
        if (session != null) {
            try {
                sessionUser = session.getAttribute("currentUser");
            } catch (Exception ignored) {
            }
        }
        log.debug("SEC-DIAG uri={} principal={} authorities={} sessionUserPresent={} method={}",
                uri, principal, authorities, sessionUser != null, request.getMethod());
        filterChain.doFilter(request, response);
    }
}
