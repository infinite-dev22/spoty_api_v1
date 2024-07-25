package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.interfaces.TenantService;
import io.nomard.spoty_api_v1.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/status")
    public ResponseEntity<ObjectNode> getSubscriptionStatus(@RequestParam String email) throws NotFoundException {
        Long userId = userService.getByEmail(email).getId();
        var trialEndDate = tenantService.getTrialEndDate(userId);
        boolean isTrial = tenantService.isTrial(userId);
        boolean canTry = tenantService.canTry(userId);
        boolean isNewTenancy = tenantService.isNewTenancy(userId);
        LocalDateTime subscriptionEndDateTime = tenantService.getSubscriptionEndDate(userId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodEnd = subscriptionEndDateTime.plusDays(getGracePeriodDays());
        LocalDateTime subscriptionWarningDate = subscriptionEndDateTime.minusDays(getGracePeriodDays());

        ObjectNode response = objectMapper.createObjectNode();
        response.put("isTrial", isTrial);
        response.put("canTry", canTry);
        response.put("isNewTenancy", isNewTenancy);
        response.put("subscriptionEndDate", tenantService.getSubscriptionEndDate(userId).toString());
        response.put("trialEndDate", trialEndDate != null ? trialEndDate.toString() : null);

        if (now.isAfter(subscriptionEndDateTime)) {
            if (now.isBefore(gracePeriodEnd)) {
                response.put("status", "gracePeriod");
                response.put("message", "Subscription expired, please renew.");
            } else {
                response.put("status", "expired");
                response.put("message", "Subscription expired and grace period is over, access denied.");
            }
        } else if (now.isAfter(subscriptionWarningDate)) {
            response.put("status", "aboutToExpire");
            response.put("message", "Subscription is about to expire, please renew.");
        } else {
            response.put("status", "active");
            response.put("message", "Subscription is active.");
        }

        return ResponseEntity.ok(response);
    }

    private int getGracePeriodDays() {
        return 7; // Retrieve grace period duration from configuration
    }
}
