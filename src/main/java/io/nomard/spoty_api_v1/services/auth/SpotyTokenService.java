package io.nomard.spoty_api_v1.services.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class SpotyTokenService {
    private static final Duration JWT_TOKEN_VALIDITY = Duration.ofHours(15);
    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ObjectMapper objectMapper;

    public SpotyTokenService(@Value("${jwt.secret}") final String secret) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
    }

    public String generateToken(final UserDetails userDetails) {
        var user = userRepo.findUserByEmail(userDetails.getUsername());
        final Instant now = Instant.now();
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer("SpotyApi")
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(JWT_TOKEN_VALIDITY.toMillis()))
                .sign(this.hmac512);
    }

    public String validateTokenAndGetUsername(final String token) {
        try {
            var verifiedToken = verifier.verify(token);

            return verifiedToken.getSubject();
        } catch (final JWTVerificationException verificationEx) {
            return "{" +
                    "\t\"status\": \"TOKEN INVALID\",\n" +
                    "\t\"message\": \"" + verificationEx.getMessage() + "\"" +
                    "}";
        }
    }

}