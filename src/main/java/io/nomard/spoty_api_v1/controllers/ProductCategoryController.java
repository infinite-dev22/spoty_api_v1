package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.ProductCategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ProductCategoryController {
    @Autowired
    private ProductCategoryServiceImpl productCategoryService;


    @GetMapping("/product_categories")
    public List<ProductCategory> getAll() {
        return productCategoryService.getAll();
    }

    @PostMapping("/product_category")
    public ProductCategory getById(@RequestBody Long id) throws NotFoundException {
        return productCategoryService.getById(id);
    }

    @PostMapping("/product_categories/search")
    public List<ProductCategory> getByContains(@RequestBody String search) {
        return productCategoryService.getByContains(search);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/product_category/add")
    public ProductCategory save(@Valid @RequestBody ProductCategory productCategory) {
        productCategory.setCreatedAt(new Date());
        return productCategoryService.save(productCategory);
    }

    @PutMapping("/product_category/update")
    public ProductCategory update(@RequestBody Long id, @Valid @RequestBody ProductCategory productCategory) {
        productCategory.setId(id);
        productCategory.setUpdatedAt(new Date());
        return productCategoryService.update(id, productCategory);
    }

    @PostMapping("/product_category/delete")
    public String delete(@RequestBody Long id) {
        return productCategoryService.delete(id);
    }
}
