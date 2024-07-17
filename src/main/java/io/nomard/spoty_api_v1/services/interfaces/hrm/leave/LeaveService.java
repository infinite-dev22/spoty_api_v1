package io.nomard.spoty_api_v1.services.interfaces.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LeaveService {
    Flux<PageImpl<Leave>> getAll(int pageNo, int pageSize);

    Mono<Leave> getById(Long id);

    Flux<Leave> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Leave leave);

    Mono<ResponseEntity<ObjectNode>> update(Leave leave);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
