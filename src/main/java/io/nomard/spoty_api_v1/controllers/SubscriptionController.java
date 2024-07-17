package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.services.interfaces.TenantService;
import io.nomard.spoty_api_v1.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
    public Mono<ResponseEntity<ObjectNode>> getSubscriptionStatus(@RequestParam String email) {
        return userService.getByEmail(email)
                .flatMap(user -> {
                    Long userId = user.getId();
                    Mono<Date> subscriptionEndDateMono = tenantService.getSubscriptionEndDate(userId);
                    Mono<Date> trialEndDateMono = tenantService.getTrialEndDate(userId);
                    Mono<Boolean> isTrialMono = tenantService.isTrial(userId);
                    Mono<Boolean> canTryMono = tenantService.canTry(userId);
                    Mono<Boolean> isNewTenancyMono = tenantService.isNewTenancy(userId);

                    return Mono.zip(subscriptionEndDateMono, trialEndDateMono, isTrialMono, canTryMono, isNewTenancyMono)
                            .flatMap(tuple -> {
                                Date subscriptionEndDate = tuple.getT1();
                                Date trialEndDate = tuple.getT2();
                                boolean isTrial = tuple.getT3();
                                boolean canTry = tuple.getT4();
                                boolean isNewTenancy = tuple.getT5();

                                LocalDateTime subscriptionEndDateTime = LocalDateTime.ofInstant(subscriptionEndDate.toInstant(), ZoneId.systemDefault());
                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime gracePeriodEnd = subscriptionEndDateTime.plusDays(getGracePeriodDays());
                                LocalDateTime subscriptionWarningDate = subscriptionEndDateTime.minusDays(getGracePeriodDays());

                                ObjectNode response = objectMapper.createObjectNode();
                                response.put("isTrial", isTrial);
                                response.put("canTry", canTry);
                                response.put("isNewTenancy", isNewTenancy);
                                response.put("subscriptionEndDate", subscriptionEndDate.toString());
                                response.put("trialEndDate", trialEndDate.toString());

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

                                return Mono.just(ResponseEntity.ok(response));
                            });
                });
    }


    private int getGracePeriodDays() {
        return 7; // Retrieve grace period duration FROM configuration
    }
}
