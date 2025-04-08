package com.example.UserProfileManager.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TokenManager {

    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 4; // 4 minutes
    private final long EXPIRATION_DATE = 1000L * 60 * 2; // 2 minutes

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = Map.of("roles", roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DATE))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignKey())
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}