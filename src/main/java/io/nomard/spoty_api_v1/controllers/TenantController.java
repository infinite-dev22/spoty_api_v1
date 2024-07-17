package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.TenantServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@RequestMapping("/tenants")
public class TenantController {
    @Autowired
    private TenantServiceImpl tenantService;

    @GetMapping("/all")
    public Flux<Tenant> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                               @RequestParam(defaultValue = "50") Integer pageSize) {
        return tenantService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Tenant> getById(@RequestBody FindModel findModel) {
        return tenantService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Tenant tenant) {
        tenant.setCreatedAt(new Date());
        return tenantService.save(tenant);
    }

    @PutMapping("/start/trial")
    public Mono<ResponseEntity<ObjectNode>> startTrial(@RequestBody FindModel findModel) {
        return tenantService.startTrial(findModel.getId());
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Tenant tenant) {
        return tenantService.update(tenant);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return tenantService.delete(findModel.getId());
    }
}
