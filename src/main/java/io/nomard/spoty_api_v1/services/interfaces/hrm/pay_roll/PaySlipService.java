package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PaySlipService {
    Flux<PageImpl<PaySlip>> getAll(int pageNo, int pageSize);

    Mono<PaySlip> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(PaySlip paySlip);

    Mono<ResponseEntity<ObjectNode>> update(PaySlip paySlip);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
