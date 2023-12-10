package io.nomard.spoty_api_v1.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;

@Service
public class SpotyTokenService {

    private static final Duration JWT_TOKEN_VALIDITY = Duration.ofHours(15);

    private final Algorithm hmac512;
    private final JWTVerifier verifier;

    public SpotyTokenService(@Value("${jwt.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    public String generateToken(final UserDetails userDetails) {
        final Instant now = Instant.now();
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer("app")
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(JWT_TOKEN_VALIDITY.toMillis()))
                .sign(this.hmac512);
    }

    public String validateTokenAndGetUsername(final String token) {
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException verificationEx) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "{" +
                    "\t\"status\": \"TOKEN INVALID\",\n" +
                    "\t\"message\": \"" + verificationEx.getMessage() + "\"" +
                    "}"
            );
        }
    }

}