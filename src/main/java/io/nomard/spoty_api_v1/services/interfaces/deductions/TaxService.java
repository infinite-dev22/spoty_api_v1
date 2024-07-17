package io.nomard.spoty_api_v1.services.interfaces.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface TaxService {
    Flux<PageImpl<Tax>> getAll(int pageNo, int pageSize);

    Mono<Tax> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(Tax tax);

    Mono<ResponseEntity<ObjectNode>> update(Tax tax);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
