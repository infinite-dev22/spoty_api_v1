package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SaleTermAndConditionService {
    Flux<PageImpl<SaleTermAndCondition>> getAll(int pageNo, int pageSize);

    Mono<SaleTermAndCondition> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(SaleTermAndCondition saleTermAndCondition);

    Mono<ResponseEntity<ObjectNode>> update(SaleTermAndCondition saleTermAndCondition);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
