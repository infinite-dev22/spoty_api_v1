package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface PermissionService {
    Flux<PageImpl<Permission>> getAll(int pageNo, int pageSize);

    Mono<Permission> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(Permission permission);

    Mono<ResponseEntity<ObjectNode>> update(Permission permission);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
