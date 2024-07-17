package io.nomard.spoty_api_v1.services.interfaces.deductions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface DiscountService {
    Flux<PageImpl<Discount>> getAll(int pageNo, int pageSize);

    Mono<Discount> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(Discount discount);

    Mono<ResponseEntity<ObjectNode>> update(Discount discount);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
