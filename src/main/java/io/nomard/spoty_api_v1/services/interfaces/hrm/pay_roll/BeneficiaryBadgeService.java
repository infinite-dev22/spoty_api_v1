package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface BeneficiaryBadgeService {
    Flux<PageImpl<BeneficiaryBadge>> getAll(int pageNo, int pageSize);

    Mono<BeneficiaryBadge> getById(Long id);

    Flux<BeneficiaryBadge> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(BeneficiaryBadge beneficiaryBadge);

    Mono<ResponseEntity<ObjectNode>> update(BeneficiaryBadge beneficiaryBadge);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
