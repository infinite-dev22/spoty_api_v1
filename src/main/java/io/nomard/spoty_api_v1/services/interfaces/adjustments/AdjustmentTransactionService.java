package io.nomard.spoty_api_v1.services.interfaces.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentTransaction;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AdjustmentTransactionService {
    Mono<AdjustmentTransaction> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(AdjustmentDetail adjustmentDetail);
}
