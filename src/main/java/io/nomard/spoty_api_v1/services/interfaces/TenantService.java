package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface TenantService {
    Flux<Tenant> getAll(int pageNo, int pageSize);

    Mono<Tenant> getById(Long id);

    Mono<Date> getSubscriptionEndDate(Long id);

    Mono<Date> getTrialEndDate(Long id);

    Mono<Boolean> isTrial(Long id);

    Mono<Boolean> canTry(Long id);

    Mono<Boolean> isNewTenancy(Long id);

    Mono<Boolean> isInGracePeriod(Long userId)  throws NotFoundException;

    Mono<ResponseEntity<ObjectNode>> startTrial(Long tenantId);

    Mono<ResponseEntity<ObjectNode>> save(Tenant tenant);

    Mono<ResponseEntity<ObjectNode>> update(Tenant tenant);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);
}
