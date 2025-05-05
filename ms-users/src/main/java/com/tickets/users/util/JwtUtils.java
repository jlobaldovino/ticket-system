package com.tickets.users.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String extractEmail(String token) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(cleanToken)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            return "anonimo";
        }
    }
}
