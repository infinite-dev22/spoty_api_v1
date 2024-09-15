package io.nomard.spoty_api_v1.filters;

import io.nomard.spoty_api_v1.services.auth.SpotyTokenService;
import io.nomard.spoty_api_v1.services.auth.SpotyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SpotyRequestFilter extends OncePerRequestFilter {
    @Autowired
    public SpotyTokenService spotyTokenService;
    @Autowired
    public SpotyUserDetailsService spotyUserDetailsService;

    @Override
    protected void doFilterInternal(final @NotNull HttpServletRequest request, final @NotNull HttpServletResponse response,
                                    final @NotNull FilterChain chain) throws ServletException, IOException {
        // look for Bearer auth header
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            chain.doFilter(request, response);
            return;
        }

        final String token = header.substring(7);
        final String username = spotyTokenService.validateTokenAndGetUsername(token);
        if (username == null) {
            // validation failed or token expired
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            chain.doFilter(request, response);
            return;
        }

        // set user details on spring security context
        try {
            final UserDetails userDetails = spotyUserDetailsService.loadUserByUsername(username);
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }


        // continue with authenticated user
        chain.doFilter(request, response);
    }

}