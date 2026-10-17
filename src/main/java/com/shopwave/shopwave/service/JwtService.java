
package com.shopwave.shopwave.service;

import com.shopwave.shopwave.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service // Registers this class as a Spring-managed service.
public class JwtService {

    // Secret key used to sign JWTs and verify their authenticity.
    private final SecretKey signingKey;

    // Access-token lifetime, configured in application.properties.
    private final long accessTokenMinutes;

    // Spring injects the values from application.properties.
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes) {

        // The secret is stored as Base64 text in the environment variable.
        // Decode it into bytes and convert those bytes into a signing key.
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        // Store the configured token lifetime for later use.
        this.accessTokenMinutes = accessTokenMinutes;
    }

    // Creates and returns a signed JWT for the authenticated user.
    public String generateToken(Users user) {

        // Capture the current time so issuance and expiration use one reference.
        Instant now = Instant.now();

        return Jwts.builder()

                // The subject identifies the user represented by this token.
                .subject(user.getUserName())

                // Add the user's role as a custom claim, e.g. "USER" or "ADMIN".
                .claim("role", user.getRole().name())

                // Record when the token was issued.
                .issuedAt(Date.from(now))

                // Set when the token expires, based on the configured lifetime.
                .expiration(Date.from(
                        now.plus(accessTokenMinutes, ChronoUnit.MINUTES)
                ))

                // Sign the token so the server can detect tampering.
                .signWith(signingKey)

                // Build the final JWT string to return to the client.
                .compact();
    }


    public String extractUsername(String token) {

        // Parse the token, verify its signature using our secret key,
        // and extract the claims (the token's payload).
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
