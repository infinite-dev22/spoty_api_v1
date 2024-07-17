package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PaySlipTypeService {
    Flux<PageImpl<PaySlipType>> getAll(int pageNo, int pageSize);

    Mono<PaySlipType> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(PaySlipType paySlipType);

    Mono<ResponseEntity<ObjectNode>> update(PaySlipType paySlipType);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
