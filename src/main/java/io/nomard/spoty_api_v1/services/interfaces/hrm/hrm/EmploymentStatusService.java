package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmploymentStatusService {
    Flux<PageImpl<EmploymentStatus>> getAll(int pageNo, int pageSize);

    Mono<EmploymentStatus> getById(Long id);

    Flux<EmploymentStatus> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(EmploymentStatus employmentStatus);

    Mono<ResponseEntity<ObjectNode>> update(EmploymentStatus employmentStatus);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
