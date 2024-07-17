package io.nomard.spoty_api_v1.configs;

import io.nomard.spoty_api_v1.filters.SpotyRequestFilter;
import io.nomard.spoty_api_v1.security.SpotyAuthEntryPoint;
import io.nomard.spoty_api_v1.services.auth.SpotyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final String[] WHITE_LIST_URL = {"/auth/**", "/error/**", "/error"};

    @Autowired
    private SpotyAuthEntryPoint spotyAuthEntryPoint;

    @Autowired
    private SpotyRequestFilter spotyRequestFilter;

    @Autowired
    private SpotyUserDetailsService spotyUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager = new UserDetailsRepositoryReactiveAuthenticationManager(spotyUserDetailsService);
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(spotyAuthEntryPoint))
                .authorizeExchange(auth -> auth
                        .pathMatchers(WHITE_LIST_URL).permitAll()
                        .anyExchange().authenticated()
                )
//                .sessionManagement(session -> session.concurrentSessions(SessionCreationPolicy.STATELESS))
                .addFilterAt(spotyRequestFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .build();
    }
}
