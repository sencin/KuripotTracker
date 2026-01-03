package com.mintblueberry.KuripotTracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}") // in milliseconds
    private long jwtExpirationMs;

    /**
     * Generate a JWT for the given email and return it.
     * No cookie is set; client must store the token and send in Authorization header.
     */
    public String generateToken(String email) {

        return Jwts.builder()
                .claim("email", email)          // custom claim
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Validate a JWT. Throws JwtException if invalid or expired.
     */
    public void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtException("JWT token is missing");
        }

        // parseSignedClaims verifies the signature and expiration
        Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token);
    }


    /**
     * Extract the email claim from a JWT.
     */
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    /**
     * Get the signing key for HMAC.
     */
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}