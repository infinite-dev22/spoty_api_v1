package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@Log
public class TenantSubscriptionServiceImpl {
    @Autowired
    private TenantServiceImpl tenantService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthServiceImpl authService;

    public ResponseEntity<ObjectNode> checkSubscriptionStatus() throws NotFoundException {
        var now = LocalDateTime.now();
        var tenantId = authService.authUser().getTenant().getId();
        var subscriptionEndDate = tenantService.getSubscriptionEndDate(tenantId);
        var gracePeriodEnd = subscriptionEndDate.plusDays(tenantService.getGracePeriodDays());
        var subscriptionWarningDate = subscriptionEndDate.minusDays(tenantService.getGracePeriodDays());

        boolean trial = tenantService.isTrial(tenantId);
        boolean activeTenancyWarning = subscriptionEndDate.isAfter(now) && subscriptionEndDate.isBefore(subscriptionWarningDate.plusDays(tenantService.getGracePeriodDays()));

        var response = objectMapper.createObjectNode();
        response.put("canTry", tenantService.canTry(tenantId));
        response.put("blockAccess", subscriptionEndDate.isBefore(now));

        if (trial && activeTenancyWarning && Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
            response.put("showTrialSoonEnds", true);
            response.put("timeLeft", ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()));
            response.put("message", "Your trial expires in " + ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()) + " days");
        } else if (trial && activeTenancyWarning && !Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
            response.put("showTrialSoonEnds", false);
            response.put("message", "Welcome to the brighter side of tech, enjoy your stay");
        }
        if (!trial && activeTenancyWarning && Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
            response.put("showSubscriptionWarning", true);
            response.put("timeLeft", ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()));
            response.put("message", "Subscription expires in " + ChronoUnit.DAYS.between(gracePeriodEnd, LocalDateTime.now()) + " days");
        } else if (!trial && activeTenancyWarning && !Objects.equals(authService.authUser().getEmail(), authService.authUser().getTenant().getEmail())) {
            response.put("showSubscriptionWarning", false);
            response.put("message", "Welcome to the brighter side of tech, enjoy your stay");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
