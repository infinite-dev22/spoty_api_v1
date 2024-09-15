package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
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

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return tenantSettingsService.delete(findModel.getId());
    }
}
