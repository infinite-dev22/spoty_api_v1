package io.nomard.spoty_api_v1.services.interfaces.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferTransaction;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransferTransactionService {
    Mono<TransferTransaction> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(TransferDetail transferDetail);

    Mono<ResponseEntity<ObjectNode>> update(TransferDetail transferDetail);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
