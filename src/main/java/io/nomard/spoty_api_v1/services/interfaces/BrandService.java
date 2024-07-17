package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface BrandService {
    Flux<PageImpl<Brand>> getAll(int pageNo, int pageSize);

    Mono<Brand> getById(Long id);

    Flux<Brand> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Brand brand);

    Mono<ResponseEntity<ObjectNode>> update(Brand data);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
