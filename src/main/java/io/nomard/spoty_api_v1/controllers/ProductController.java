package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;


    @GetMapping("/products")
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PostMapping("/product")
    public Product getById(@RequestBody Long id) throws NotFoundException {
        return productService.getById(id);
    }

    @PostMapping("/products/search")
    public List<Product> getByContains(@RequestBody String search) {
        return productService.getByContains(search);
    }

    @GetMapping("/products/stock_alert")
    public List<Product> getWarning() {
        return productService.getWarning();
    }

    @PostMapping("/product/add")
    public Product save(@Valid @RequestBody Product product) {
        product.setCreatedAt(new Date());
        return productService.save(product);
    }

    @PutMapping("/product/update")
    public Product update(@RequestBody Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        product.setUpdatedAt(new Date());
        return productService.update(id, product);
    }

    @PostMapping("/product/delete")
    public String delete(@RequestBody Long id) {
        return productService.delete(id);
    }
}
