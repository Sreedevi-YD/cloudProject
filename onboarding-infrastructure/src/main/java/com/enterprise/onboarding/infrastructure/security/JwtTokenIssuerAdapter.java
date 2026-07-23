package com.enterprise.onboarding.infrastructure.security;

import com.enterprise.onboarding.application.port.out.TokenIssuer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuerAdapter implements TokenIssuer {

    private final JwtProperties jwtProperties;

    @Override
    public IssuedToken issue(UUID userId, String username, Set<String> roles) {
        SecretKey key = signingKey();
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.expirationSeconds());

        String token = Jwts.builder()
                .subject(username)
                .id(userId.toString())
                .issuer(jwtProperties.issuer())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();

        return new IssuedToken(token, jwtProperties.expirationSeconds());
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }
}
