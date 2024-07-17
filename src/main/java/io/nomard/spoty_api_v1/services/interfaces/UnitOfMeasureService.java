package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface UnitOfMeasureService {
    Flux<PageImpl<UnitOfMeasure>> getAll(int pageNo, int pageSize);

    Mono<UnitOfMeasure> getById(Long id);

    Flux<UnitOfMeasure> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(UnitOfMeasure uom);

    Mono<ResponseEntity<ObjectNode>> update(UnitOfMeasure uom);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
