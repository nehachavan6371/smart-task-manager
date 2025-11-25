package com.smarttaskmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final long validityMs;

    public JwtUtil(@Value("${app.jwt.secret:change-me-if-production}") String secret,
                   @Value("${app.jwt.validity-ms:3600000}") long validityMs) {
        if ("change-me-if-production".equals(secret)) {
            // derive a key from default secret for convenience (not for prod)
            this.key = Keys.hmacShaKeyFor("default-unsafe-secret-change-me-please-1234567890".getBytes());
        } else {
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        }
        this.validityMs = validityMs;
    }

    public String generateToken(String username, Set<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles.stream().collect(Collectors.toList()));
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(exp).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
