package io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PurchaseReturnMasterService {
    Flux<PageImpl<PurchaseReturnMaster>> getAll(int pageNo, int pageSize);

    Mono<PurchaseReturnMaster> getById(Long id);

    Flux<PurchaseReturnMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(PurchaseReturnMaster purchaseReturnMaster);

    Mono<ResponseEntity<ObjectNode>> update(PurchaseReturnMaster purchaseReturnMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
