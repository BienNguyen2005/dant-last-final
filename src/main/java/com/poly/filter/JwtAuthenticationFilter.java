package com.poly.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.poly.service.JWTService;
import com.poly.service.UserDetailsServiceImpl;
import com.poly.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;

    public JwtAuthenticationFilter(JWTService jwtService, UserDetailsServiceImpl userDetailsService,
            UserService userService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT filter for public paths
        String path = request.getServletPath();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);
        boolean tokenValid = token != null && jwtService.validateToken(token);

        if ((!tokenValid) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String refreshToken = getRefreshTokenFromRequest(request);
            if (refreshToken != null && jwtService.validateToken(refreshToken)) {
                try {
                    String newAccessToken = userService.refreshAccessToken(refreshToken);
                    token = newAccessToken;
                    tokenValid = true;
                    if (log.isDebugEnabled()) {
                        log.debug("Auto-refreshed access token via refresh token for path {}", path);
                    }
                } catch (Exception ex) {
                    if (log.isDebugEnabled()) {
                        log.debug("Auto-refresh failed for path {}: {}", path, ex.getMessage());
                    }
                }
            } else if (refreshToken != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Refresh token present but invalid/expired for path {}", path);
                }
            }
        }

        if (tokenValid && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);
                if (role == null) {
                    role = "USER";
                }
                if (role.startsWith("ROLE_")) {
                    role = role.substring(5);
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                if (log.isDebugEnabled()) {
                    log.debug("JWT auth success: path={}, user={}, role=ROLE_{} (autoRefreshed={})", path, username,
                            role, (!tokenValid ? "false" : "?"));
                }
            } catch (Exception e) {
                log.warn("JWT auth user load failed after token validation for path {}: {}", path, e.getMessage());
            }
        } else if (!tokenValid && token != null) {
            if (log.isDebugEnabled()) {
                log.debug("JWT token invalid or expired for path {} (no refresh applied)", path);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.startsWith("/signin") ||
                path.startsWith("/signup") ||
                path.startsWith("/active-account") ||
                path.startsWith("/forgot-password") ||
                path.startsWith("/reset-password") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/fragment/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/img/") ||
                path.startsWith("/image/");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // If not in header, try to get from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
