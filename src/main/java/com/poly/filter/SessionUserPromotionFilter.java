package com.poly.filter;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.poly.entity.Users;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;

/**
 * Promotes session attribute 'currentUser' to Spring Security Authentication if
 * no authentication is yet present. This covers navigation flows where
 * login established only a session but not a JWT header in later requests.
 */
@Component
public class SessionUserPromotionFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SessionUserPromotionFilter.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Skip static resources
        String uri = request.getRequestURI();
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/img/") || uri.startsWith("/image/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object u = session.getAttribute("currentUser");
                if (u instanceof Users user) {
                    String role = user.isVaitro() ? "ADMIN" : "USER";
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            user.getIdUser(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.trace("SessionUserPromotion: promoted user {} with ROLE_{}", user.getIdUser(), role);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
