package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface RoleService {
    Flux<PageImpl<Role>> getAll(int pageNo, int pageSize);

    Mono<Role> getById(Long id);

    Flux<Role> search(String search);

    Mono<ResponseEntity<ObjectNode>> save(Role role);

    Mono<ResponseEntity<ObjectNode>> update(Role role);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
