package org.vullnet.vullnet00.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vullnet.vullnet00.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user) {
        return generateToken(UserPrincipal.fromUser(user));
    }

    public String generateToken(UserPrincipal principal) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("uid", principal.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object claim = parseClaims(token).get("uid");
        if (claim instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public boolean isTokenValid(String token, UserPrincipal principal) {
        String username = extractUsername(token);
        return username != null && username.equals(principal.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp != null && exp.before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
