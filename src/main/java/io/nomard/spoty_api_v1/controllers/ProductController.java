package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/all")
    public Flux<PageImpl<Product>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return productService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Product> getById(@RequestBody FindModel findModel) {
        return productService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Product> getByContains(@RequestBody SearchModel searchModel) {
        return productService.getByContains(searchModel.getSearch());
    }

    @GetMapping("/stock_alert")
    public Flux<Product> getWarning() {
        return productService.getWarning();
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestPart("product") Product product, @Valid @RequestPart("file") MultipartFile file) {
        return productService.save(product, file);
    }

    @PutMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestPart("product") Product product, @Valid @RequestPart("file") MultipartFile file) {
        return productService.update(product, file);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return productService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return productService.deleteMultiple(idList);
    }
}
