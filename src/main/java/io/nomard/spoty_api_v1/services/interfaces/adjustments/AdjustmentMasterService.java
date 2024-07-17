package io.nomard.spoty_api_v1.services.interfaces.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AdjustmentMasterService {
    Flux<PageImpl<AdjustmentMaster>> getAll(int pageNo, int pageSize);

    Mono<AdjustmentMaster> getById(Long id);

    Flux<AdjustmentMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(AdjustmentMaster adjustmentMaster);

    Mono<ResponseEntity<ObjectNode>> update(AdjustmentMaster adjustmentMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
