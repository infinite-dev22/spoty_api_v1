package io.nomard.spoty_api_v1.services.interfaces.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PurchaseMasterService {
    Flux<PageImpl<PurchaseMaster>> getAll(int pageNo, int pageSize);

    Mono<PurchaseMaster> getById(Long id);

    Flux<PurchaseMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(PurchaseMaster purchaseMaster);

    Mono<ResponseEntity<ObjectNode>> update(PurchaseMaster purchaseMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
