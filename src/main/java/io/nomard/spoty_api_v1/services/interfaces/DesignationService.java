package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DesignationService {
    Flux<PageImpl<Designation>> getAll(int pageNo, int pageSize);

    Mono<Designation> getById(Long id);

    Flux<Designation> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Designation designation);

    Mono<ResponseEntity<ObjectNode>> update(Designation designation);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
