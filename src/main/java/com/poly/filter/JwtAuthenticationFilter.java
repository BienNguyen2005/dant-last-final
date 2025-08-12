package com.poly.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.poly.service.JWTService;
import com.poly.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JWTService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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
        
        // Extract JWT token from request
        String token = getTokenFromRequest(request);
        
        // If token exists and there's no authentication in context
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
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
               path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/img/") ||
               path.startsWith("/image/");
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        // First try to get from Authorization header
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
        
        // If not found in header or cookies, try session attribute
        return (String) request.getSession().getAttribute("jwt_token");
    }
}
