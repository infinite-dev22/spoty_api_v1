package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Product;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public interface ProductService {
    Flux<PageImpl<Product>> getAll(int pageNo, int pageSize);

    Mono<Product> getById(Long id);

    Flux<Product> getByContains(String search);

    Flux<Product> getWarning();

    Mono<ResponseEntity<ObjectNode>> save(Product product, MultipartFile file);

    Mono<ResponseEntity<ObjectNode>> update(Product product, MultipartFile file);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
