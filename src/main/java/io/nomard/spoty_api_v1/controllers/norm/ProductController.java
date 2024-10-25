package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ProductDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ProductServiceImpl;
import io.nomard.spoty_api_v1.utils.Views;
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
    @JsonView(Views.Tiny.class)
    public Page<ProductDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return productService.getAll(pageNo, pageSize);
    }

    @GetMapping("/all/non_paged")
    public List<ProductDTO> getAllNonPaged() {
        return productService.getAllNonPaged();
    }

    @GetMapping("/single")
    public ProductDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return productService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<ProductDTO> getByContains(@RequestBody SearchModel searchModel) {
        return productService.getByContains(searchModel.getSearch());
    }

    @GetMapping("/stock_alert")
    public List<ProductDTO> getWarning() {
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
