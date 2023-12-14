package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategory> getAll();

    ProductCategory getById(Long id) throws NotFoundException;

    List<ProductCategory> getByContains(String search);

    ResponseEntity<ObjectNode> save(ProductCategory productCategory);

    ResponseEntity<ObjectNode> update(Long id, ProductCategory productCategory);

    ResponseEntity<ObjectNode> delete(Long id);
}
