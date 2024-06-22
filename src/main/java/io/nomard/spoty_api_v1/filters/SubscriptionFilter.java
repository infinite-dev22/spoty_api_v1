package io.nomard.spoty_api_v1.filters;

import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.UserRepository;
import io.nomard.spoty_api_v1.services.implementations.TenantServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class SubscriptionFilter extends OncePerRequestFilter {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TenantServiceImpl tenantService;

    @Override
    protected void doFilterInternal(final @NotNull HttpServletRequest request, final @NotNull HttpServletResponse response,
                                    final @NotNull FilterChain chain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Long userId = userRepo.findUserByEmail(username).getId();

            if (userId != null) {
                Date subscriptionEndDate;
                try {
                    subscriptionEndDate = tenantService.getSubscriptionEndDate(userId);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
                boolean isSubscriptionValid = subscriptionEndDate.after(new Date());
                boolean isInGracePeriod;
                try {
                    isInGracePeriod = tenantService.isInGracePeriod(userId);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }

                if (!isSubscriptionValid && !isInGracePeriod) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"status\":200, \n" +
                            "\"activeTenancy\": false, \n" +
                            "\"canTry\": false}");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
