package io.nomard.spoty_api_v1.services.interfaces.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RequisitionMasterService {
    Flux<PageImpl<RequisitionMaster>> getAll(int pageNo, int pageSize);

    Mono<RequisitionMaster> getById(Long id);

    Flux<RequisitionMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(RequisitionMaster requisitionMaster);

    Mono<ResponseEntity<ObjectNode>> update(RequisitionMaster requisitionMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
