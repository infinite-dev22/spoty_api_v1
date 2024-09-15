package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.logging.Level;

@Service
@Log
public class TenantSubscriptionServiceImpl {
    @Autowired
    private TenantServiceImpl tenantService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthServiceImpl authService;

    public ResponseEntity<ObjectNode> checkSubscriptionStatus() {
        try {
            var now = LocalDateTime.now();
            var gracePeriodEnd = tenantService.getSubscriptionEndDate(authService.authUser().getTenant().getId()).plusDays(getGracePeriodDays());
            var subscriptionWarningDate = tenantService.getSubscriptionEndDate(authService.authUser().getTenant().getId()).minusDays(getGracePeriodDays());

            boolean trial = tenantService.isTrial(authService.authUser().getTenant().getId());
            boolean activeTenancyWarning = tenantService.getSubscriptionEndDate(authService.authUser().getTenant().getId()).isAfter(now) && tenantService.getSubscriptionEndDate(authService.authUser().getTenant().getId()).isBefore(subscriptionWarningDate.plusDays(getGracePeriodDays()));

            var response = objectMapper.createObjectNode();
            response.put("canTry", tenantService.canTry(authService.authUser().getTenant().getId()));
            response.put("block_access", tenantService.getSubscriptionEndDate(authService.authUser().getTenant().getId()).isAfter(now));

            if (trial && activeTenancyWarning && Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
                response.put("show_trial_soon_ends", true);
                response.put("time_left", ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()));
                response.put("message", "Your trial expires in " + ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()) + " days");
            } else if (trial && activeTenancyWarning && !Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
                response.put("show_trial_soon_ends", false);
                response.put("message", "Welcome to the brighter side of tech, enjoy your stay");
            }
            if (!trial && activeTenancyWarning && Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
                response.put("show_subscription_warning", true);
                response.put("time_left", ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()));
                response.put("message", "Subscription expires in " + ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()) + " days");
            } else if (!trial && activeTenancyWarning && !Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
                response.put("show_subscription_warning", false);
                response.put("message", "Welcome to the brighter side of tech, enjoy your stay");
            }

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.log(Level.ALL, e.getMessage(), e);
            return spotyResponseImpl.custom(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }

    private int getGracePeriodDays() {
        return 7;
    }
}
