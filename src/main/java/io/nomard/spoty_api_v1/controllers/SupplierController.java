package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.SupplierServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/expense_categories")
public class SupplierController {
    @Autowired
    private SupplierServiceImpl supplierService;


    @GetMapping("/all")
    public List<Supplier> getAll() {
        return supplierService.getAll();
    }

    @PostMapping("/single")
    public Supplier getById(@RequestBody Long id) throws NotFoundException {
        return supplierService.getById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Supplier supplier) {
        supplier.setCreatedAt(new Date());
        return supplierService.save(supplier);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplier.setUpdatedAt(new Date());
        return supplierService.update(id, supplier);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return supplierService.delete(id);
    }
}
