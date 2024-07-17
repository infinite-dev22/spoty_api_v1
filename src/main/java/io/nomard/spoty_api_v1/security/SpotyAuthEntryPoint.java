package io.nomard.spoty_api_v1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SpotyAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(SpotyAuthEntryPoint.class);

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authException) {
        logger.error("Unauthorized error: {}", authException.getMessage());

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", exchange.getRequest().getPath().value());

        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes = new byte[0];
        try {
            bytes = mapper.writeValueAsBytes(body);
        } catch (IOException e) {
            logger.error("Error writing response body", e);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
