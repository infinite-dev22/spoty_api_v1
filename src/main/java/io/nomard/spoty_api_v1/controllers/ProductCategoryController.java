package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ProductCategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/product/categories")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryServiceImpl productCategoryService;


    @GetMapping("/all")
    public List<ProductCategory> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "50") Integer pageSize) {
        return productCategoryService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public ProductCategory getById(@RequestBody FindModel findModel) throws NotFoundException {
        return productCategoryService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<ProductCategory> getByContains(@RequestBody SearchModel searchModel) {
        return productCategoryService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody ProductCategory productCategory) {
        productCategory.setCreatedAt(new Date());
        return productCategoryService.save(productCategory);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody ProductCategory productCategory) throws NotFoundException {
        return productCategoryService.update(productCategory);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return productCategoryService.delete(findModel.getId());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return productCategoryService.deleteMultiple(idList);
    }
}
