package io.nomard.spoty_api_v1.services.interfaces.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInTransaction;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockInTransactionService {
    Mono<StockInTransaction> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(StockInDetail stockInDetail);

    Mono<ResponseEntity<ObjectNode>> update(StockInDetail stockInDetail);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
