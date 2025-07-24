package com.poly.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.poly.entity.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	 public static final String SERECT = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	    //Taok JWT voi cac claims
	    private String createTokenUser(Map<String, Objects> claims, Users user){
	        return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(user.getIdUser())
	                .claim("username", user.getIdUser())
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + 600*60*1000)) //Jwt hwt han sau 1 tieng
	                .signWith(SignatureAlgorithm.HS256, getSignKey())
	                .compact();
	    }


	    public String generateTokenUser(Users user){
	        Map<String, Objects> claims = new HashMap<>();
	        return createTokenUser(claims, user);
	    }

	    private Key getSignKey(){
	        byte[] keyByte = Decoders.BASE64.decode(SERECT);
	        return Keys.hmacShaKeyFor(keyByte);
	    }

	    // Trích xuất thông tin
	    private Claims extractAllClaims(String token){
	        return Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(token).getBody();
	    }

	    // Trích xuất TT cho 1 claims
	    public <T>  T extractClaim(String token, Function<Claims, T> claimsTFunction){
	        final Claims claims = extractAllClaims(token);
	        return claimsTFunction.apply(claims);
	    }

	    // Kiem tra Token het han
	    public Date exTractExpiriration(String token){
	        return extractClaim(token,Claims::getExpiration);
	    }

	    //Lay ra username
	    public String extractUsername(String token) {
	        return extractClaim(token, Claims::getSubject);

	    }
	    // Kiểm tra cái JWT đã hết hạn
	    public Boolean isTokenExpired(String token){
	        return exTractExpiriration(token).before(new Date());
	    }

	    
}
