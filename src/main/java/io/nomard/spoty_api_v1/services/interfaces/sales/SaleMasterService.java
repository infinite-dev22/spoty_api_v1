package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SaleMasterService {
    Flux<PageImpl<SaleMaster>> getAll(int pageNo, int pageSize);

    Mono<SaleMaster> getById(Long id);

    Flux<SaleMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(SaleMaster saleMaster);

    Mono<ResponseEntity<ObjectNode>> update(SaleMaster saleMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
