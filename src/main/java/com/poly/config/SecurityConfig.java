package com.poly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.poly.filter.JwtAuthenticationFilter;
import com.poly.security.AccessDeniedHandlerImpl;
import com.poly.filter.SecurityDiagnosticsFilter;
import com.poly.security.JwtAuthEntryPoint;
import com.poly.filter.SessionUserPromotionFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    private final SecurityDiagnosticsFilter securityDiagnosticsFilter;
    private final SessionUserPromotionFilter sessionUserPromotionFilter;

    public SecurityConfig(
            JwtAuthEntryPoint jwtAuthEntryPoint,
            AccessDeniedHandlerImpl accessDeniedHandler,
            SecurityDiagnosticsFilter securityDiagnosticsFilter,
            SessionUserPromotionFilter sessionUserPromotionFilter) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.securityDiagnosticsFilter = securityDiagnosticsFilter;
        this.sessionUserPromotionFilter = sessionUserPromotionFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
    auth.requestMatchers("/", "/signup", "/signin", "/active-account", "/forgot-password", 
            "/reset-password", "/access-denied", "/error", "/api/auth/refresh", "/css/**", "/js/**", "/img/**", "/image/**", "/fragment/**")
                    .permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            );
        
    // Order: JWT auth first (before UsernamePasswordAuthenticationFilter), then session promotion, then diagnostics
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(sessionUserPromotionFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(securityDiagnosticsFilter, SessionUserPromotionFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
