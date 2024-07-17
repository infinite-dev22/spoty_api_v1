package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.SupplierServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    @Autowired
    private SupplierServiceImpl supplierService;

    @GetMapping("/all")
    public Flux<PageImpl<Supplier>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "50") Integer pageSize) {
        return supplierService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Supplier> getById(@RequestBody FindModel findModel) {
        return supplierService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Supplier supplier) {
        supplier.setCreatedAt(new Date());
        return supplierService.save(supplier);
    }

    @GetMapping("/search")
    public Flux<Supplier> getByContains(@RequestBody SearchModel searchModel) {
        return supplierService.getByContains(searchModel.getSearch());
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Supplier supplier) {
        return supplierService.update(supplier);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return supplierService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return supplierService.deleteMultiple(idList);
    }
}
