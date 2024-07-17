package io.nomard.spoty_api_v1.services.interfaces.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransferMasterService {
    Flux<PageImpl<TransferMaster>> getAll(int pageNo, int pageSize);

    Mono<TransferMaster> getById(Long id);

    Flux<UnitOfMeasure> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(TransferMaster transferMaster);

    Mono<ResponseEntity<ObjectNode>> update(TransferMaster transferMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
