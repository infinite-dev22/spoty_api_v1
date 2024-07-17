package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public interface ExpenseService {
    Flux<PageImpl<Expense>> getAll(int pageNo, int pageSize);

    Mono<Expense> getById(Long id);

    Flux<Expense> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Expense expense);

    Mono<ResponseEntity<ObjectNode>> update(Expense expense);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
