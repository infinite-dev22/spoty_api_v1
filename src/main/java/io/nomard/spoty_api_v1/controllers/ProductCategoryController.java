package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ProductCategoryServiceImpl;
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
@RequestMapping("/product/categories")
public class ProductCategoryController {
    @Autowired
    private ProductCategoryServiceImpl productCategoryService;

    @GetMapping("/all")
    public Flux<PageImpl<ProductCategory>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "50") Integer pageSize) {
        return productCategoryService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<ProductCategory> getById(@RequestBody FindModel findModel) {
        return productCategoryService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<ProductCategory> getByContains(@RequestBody SearchModel searchModel) {
        return productCategoryService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody ProductCategory productCategory) {
        productCategory.setCreatedAt(new Date());
        return productCategoryService.save(productCategory);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody ProductCategory productCategory) {
        return productCategoryService.update(productCategory);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return productCategoryService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return productCategoryService.deleteMultiple(idList);
    }
}
