package io.nomard.spoty_api_v1.filters;

import io.nomard.spoty_api_v1.services.auth.SpotyTokenService;
import io.nomard.spoty_api_v1.services.auth.SpotyUserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SpotyRequestFilter implements WebFilter {

    @Autowired
    private SpotyTokenService spotyTokenService;

    @Autowired
    private SpotyUserDetailsService spotyUserDetailsService;

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = header.substring(7);
        String username = spotyTokenService.validateTokenAndGetUsername(token);

        if (username == null) {
            return chain.filter(exchange);
        }

        return spotyUserDetailsService.findByUsername(username)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException(username))))
                .flatMap(userDetails -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                });
    }
}
