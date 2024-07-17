package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface SupplierService {
    Flux<PageImpl<Supplier>> getAll(int pageNo, int pageSize);

    Mono<Supplier> getById(Long id);

    Flux<Supplier> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Supplier supplier);

    Mono<ResponseEntity<ObjectNode>> update(Supplier supplier);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
