package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.entities.TenantSettings;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.TenantSettingsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tenant/settings")
public class TenantSettingsController {
    @Autowired
    private TenantSettingsServiceImpl tenantSettingsService;

    @GetMapping("/get")
    public TenantSettings getSettings() {
        return tenantSettingsService.getSettings();
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody TenantSettings tenantSettings) {
        return tenantSettingsService.save(tenantSettings);
    }

    @PostMapping("/add/reviewer")
    public ResponseEntity<ObjectNode> addReviewer(@Valid @RequestBody Reviewer reviewer) {
        return tenantSettingsService.addReviewer(reviewer);
    }

    @PostMapping("/remove/reviewer")
    public ResponseEntity<ObjectNode> removeReviewer(@Valid @RequestBody FindModel findModel) {
        return tenantSettingsService.removeReviewer(findModel);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody TenantSettings tenantSettings) {
        return tenantSettingsService.update(tenantSettings);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return tenantSettingsService.delete(findModel.getId());
    }
}
