package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/all")
    public Page<Product> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return productService.getAll(pageNo, pageSize);
    }

    @GetMapping("/all/non_paged")
    public ArrayList<Product> getAllNonPaged() {
        return productService.getAllNonPaged();
    }

    @GetMapping("/single")
    public Product getById(@RequestBody FindModel findModel) throws NotFoundException {
        return productService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<Product> getByContains(@RequestBody SearchModel searchModel) {
        return productService.getByContains(searchModel.getSearch());
    }

    @GetMapping("/stock_alert")
    public List<Product> getWarning() {
        return productService.getWarning();
    }

    @PostMapping(path = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ObjectNode> save(@Valid @RequestPart("product") Product product, @Valid @RequestPart("file") MultipartFile file) {
        return productService.save(product, file);
    }

    @PutMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ObjectNode> update(@Valid @RequestPart("product") Product product, @Valid @RequestPart("file") MultipartFile file) throws NotFoundException {
        return productService.update(product, file);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return productService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return productService.deleteMultiple(idList);
    }
}
