package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.ProductCategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/product_categories")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryServiceImpl productCategoryService;


    @GetMapping("/all")
    public List<ProductCategory> getAll() {
        return productCategoryService.getAll();
    }

    @PostMapping("/single")
    public ProductCategory getById(@RequestBody Long id) throws NotFoundException {
        return productCategoryService.getById(id);
    }

    @PostMapping("/search")
    public List<ProductCategory> getByContains(@RequestBody String search) {
        return productCategoryService.getByContains(search);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody ProductCategory productCategory) {
        productCategory.setCreatedAt(new Date());
        return productCategoryService.save(productCategory);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody ProductCategory productCategory) {
        productCategory.setId(id);
        productCategory.setUpdatedAt(new Date());
        return productCategoryService.update(id, productCategory);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return productCategoryService.delete(id);
    }
}
