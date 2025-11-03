package com.example.techzone.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    private long jwtExpirationTime;

    private String secret;

    private final Key signingKey;

    public JwtTokenProvider(@Value("${app.jwt.expiration}") long jwtExpirationTime,
    @Value("${app.jwt.secret}")String secret) {
        this.jwtExpirationTime = jwtExpirationTime;
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String email, List<String> roles) {
        List<String> formattedRoles = roles.stream().map(role -> "ROLE_" + role).toList();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationTime);

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", formattedRoles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Object rolesObj = parseClaims(token).get("roles");
        if (rolesObj instanceof List<?>) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(rolesObj, new TypeReference<List<String>>() {});
        }
        return new ArrayList<>();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            logger.warn("Invalid JWT token: {}", ex.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
