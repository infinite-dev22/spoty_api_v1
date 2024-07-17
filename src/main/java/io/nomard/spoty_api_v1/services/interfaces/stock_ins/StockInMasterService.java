package io.nomard.spoty_api_v1.services.interfaces.stock_ins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockInMasterService {
    Flux<PageImpl<StockInMaster>> getAll(int pageNo, int pageSize);

    Mono<StockInMaster> getById(Long id);

    Flux<StockInMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(StockInMaster stockInMaster);

    Mono<ResponseEntity<ObjectNode>> update(StockInMaster stockInMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
