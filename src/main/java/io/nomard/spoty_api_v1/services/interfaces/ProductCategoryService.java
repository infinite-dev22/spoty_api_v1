package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface ProductCategoryService {
    Flux<PageImpl<ProductCategory>> getAll(int pageNo, int pageSize);

    Mono<ProductCategory> getById(Long id);

    Flux<ProductCategory> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(ProductCategory productCategory);

    Mono<ResponseEntity<ObjectNode>> update(ProductCategory productCategory);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
