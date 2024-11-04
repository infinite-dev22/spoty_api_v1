package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.ProductCategoryDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface ProductCategoryService {
    Page<ProductCategoryDTO> getAll(int pageNo, int pageSize);

    ProductCategoryDTO getById(Long id) throws NotFoundException;

    List<ProductCategoryDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(ProductCategory productCategory);

    ResponseEntity<ObjectNode> update(ProductCategory productCategory) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
