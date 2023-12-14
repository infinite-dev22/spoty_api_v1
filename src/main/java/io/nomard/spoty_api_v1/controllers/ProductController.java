package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;


    @GetMapping("/all")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PostMapping("/single")
    public Product getById(@RequestBody Long id) throws NotFoundException {
        return productService.getById(id);
    }

    @PostMapping("/search")
    public List<Product> getByContains(@RequestBody String search) {
        return productService.getByContains(search);
    }

    @GetMapping("/stock_alert")
    public List<Product> getWarning() {
        return productService.getWarning();
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Product product) {
        product.setCreatedAt(new Date());
        return productService.save(product);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        product.setUpdatedAt(new Date());
        return productService.update(id, product);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return productService.delete(id);
    }
}
