package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BeneficiaryTypeService {
    Flux<PageImpl<BeneficiaryType>> getAll(int pageNo, int pageSize);

    Mono<BeneficiaryType> getById(Long id);

    Flux<BeneficiaryType> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(BeneficiaryType beneficiaryType);

    Mono<ResponseEntity<ObjectNode>> update(BeneficiaryType beneficiaryType);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
