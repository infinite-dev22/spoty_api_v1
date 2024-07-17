package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface BranchService {
    Flux<PageImpl<Branch>> getAll(int pageNo, int pageSize);

    Mono<Branch> getById(Long id);

    Flux<Branch> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Branch branch);

    Mono<ResponseEntity<ObjectNode>> update(Branch branch);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
