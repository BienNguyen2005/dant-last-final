package com.poly.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.poly.entity.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JWTService {
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Token expiration times in milliseconds
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days
    private static final long PASSWORD_RESET_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30 minutes

    // Tạo JWT với các claims
    private String createTokenUser(Map<String, Object> claims, Users user, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getIdUser())
                .claim("username", user.getIdUser())
                .claim("role", user.isVaitro() ? "ADMIN" : "USER")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        return createTokenUser(claims, user, ACCESS_TOKEN_EXPIRATION);
    }
    
    public String generateRefreshToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        claims.put("tokenId", UUID.randomUUID().toString());
        return createTokenUser(claims, user, REFRESH_TOKEN_EXPIRATION);
    }

    public String generateTokenUser(Users user) {
        Map<String, Object> claims = new HashMap<>();
        return createTokenUser(claims, user, PASSWORD_RESET_TOKEN_EXPIRATION);
    }

    private Key getSignKey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyByte);
    }

    // Trích xuất thông tin
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Trích xuất TT cho 1 claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Kiểm tra Token hết hạn
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Lấy ra username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // Lấy vai trò người dùng
    public String extractRole(String token) {
        try {
            return (String) extractAllClaims(token).get("role");
        } catch (Exception e) {
            return "USER"; // Default role if not found
        }
    }

    // Kiểm tra cái JWT đã hết hạn
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    // Validate token for a specific user
    public boolean validateToken(String token, Users user) {
        final String username = extractUsername(token);
        return (username.equals(user.getIdUser()) && !isTokenExpired(token));
    }
    
    // Create a token specifically for password reset
    public String generatePasswordResetToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose", "password_reset");
        return createTokenUser(claims, user, PASSWORD_RESET_TOKEN_EXPIRATION);
    }
}
