package io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SaleReturnMasterService {
    Flux<PageImpl<SaleReturnMaster>> getAll(int pageNo, int pageSize);

    Mono<SaleReturnMaster> getById(Long id);

    Flux<SaleReturnMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(SaleReturnMaster saleReturnMaster);

    Mono<ResponseEntity<ObjectNode>> update(SaleReturnMaster saleReturnMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
