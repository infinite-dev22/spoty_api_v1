package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.TenantServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
public class TenantController {
    @Autowired
    private TenantServiceImpl tenantService;

    @GetMapping("/all")
    public Page<Tenant> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                               @RequestParam(defaultValue = "50") Integer pageSize) {
        return tenantService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Tenant getById(@RequestBody FindModel findModel) throws NotFoundException {
        return tenantService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Tenant tenant) {
        return tenantService.save(tenant);
    }

    @PutMapping("/start/trial")
    public ResponseEntity<ObjectNode> startTrial(@RequestBody FindModel findModel) throws NotFoundException {
        return tenantService.startTrial(findModel.getId());
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Tenant tenant) throws NotFoundException {
        return tenantService.update(tenant);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return tenantService.delete(findModel.getId());
    }
}
