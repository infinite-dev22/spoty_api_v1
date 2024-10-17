package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.TenantSettings;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import org.springframework.http.ResponseEntity;

public interface TenantSettingsService {
    TenantSettings getSettings() throws NotFoundException;

    ResponseEntity<ObjectNode> save(TenantSettings tenantSettings);

    ResponseEntity<ObjectNode> update(TenantSettings tenantSettings);

    ResponseEntity<ObjectNode> addReviewer(Reviewer reviewer);

    ResponseEntity<ObjectNode> removeReviewer(FindModel findModel);

    ResponseEntity<ObjectNode> delete(Long id);
}
