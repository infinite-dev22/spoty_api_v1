package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface CurrencyService {
    Flux<PageImpl<Currency>> getAll(int pageNo, int pageSize);

    Mono<Currency> getById(Long id);

    Flux<Currency> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Currency currency);

    Mono<ResponseEntity<ObjectNode>> update(Currency currency);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
