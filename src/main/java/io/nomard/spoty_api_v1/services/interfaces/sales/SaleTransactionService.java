package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SaleTransactionService {
    Flux<SaleTransaction> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(SaleDetail saleDetail);

    Mono<ResponseEntity<ObjectNode>> saveReturn(SaleReturnDetail saleReturnDetail);
}
