package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface TenantService {
    List<Tenant> getAll(int pageNo, int pageSize);

    Tenant getById(Long id) throws NotFoundException;

    LocalDateTime getSubscriptionEndDate(Long id) throws NotFoundException;

    LocalDateTime getTrialEndDate(Long id) throws NotFoundException;

    boolean isTrial(Long id) throws NotFoundException;

    boolean canTry(Long id) throws NotFoundException;

    boolean isNewTenancy(Long id) throws NotFoundException;

    boolean isInGracePeriod(Long userId) throws NotFoundException;

    ResponseEntity<ObjectNode> startTrial(Long tenantId) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Tenant tenant);

    ResponseEntity<ObjectNode> update(Tenant tenant) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
