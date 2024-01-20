package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.SupplierServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    @Autowired
    private SupplierServiceImpl supplierService;


    @GetMapping("/all")
    public List<Supplier> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        return supplierService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Supplier getById(@RequestBody FindModel findModel) throws NotFoundException {
        return supplierService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Supplier supplier) {
        supplier.setCreatedAt(new Date());
        return supplierService.save(supplier);
    }

    @GetMapping("/search")
    public List<Supplier> getByContains(@RequestBody SearchModel searchModel) {
        return supplierService.getByContains(searchModel.getSearch());
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Supplier supplier) throws NotFoundException {
        return supplierService.update(supplier);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return supplierService.delete(findModel.getId());
    }
}
