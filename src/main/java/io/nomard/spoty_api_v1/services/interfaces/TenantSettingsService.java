package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.TenantSettings;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

public interface TenantSettingsService {
    TenantSettings getSettings() throws NotFoundException;

    ResponseEntity<ObjectNode> save(TenantSettings customer);

    ResponseEntity<ObjectNode> delete(Long id);
}
