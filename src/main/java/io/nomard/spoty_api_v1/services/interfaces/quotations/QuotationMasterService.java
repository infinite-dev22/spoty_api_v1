package io.nomard.spoty_api_v1.services.interfaces.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface QuotationMasterService {
    Flux<PageImpl<QuotationMaster>> getAll(int pageNo, int pageSize);

    Mono<QuotationMaster> getById(Long id);

    Flux<QuotationMaster> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(QuotationMaster quotationMaster);

    Mono<ResponseEntity<ObjectNode>> update(QuotationMaster quotationMaster);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
