package io.nomard.spoty_api_v1.controllers.client.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.TenantSubscriptionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription")
public class TenantSubscriptionController {
    @Autowired
    private TenantSubscriptionServiceImpl subscriptionService;

    @GetMapping("/status")
    public ResponseEntity<ObjectNode> checkSubscriptionStatus() throws NotFoundException {
        return subscriptionService.checkSubscriptionStatus();
    }
}
