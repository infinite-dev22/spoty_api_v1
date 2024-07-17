package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface ExpenseCategoryService {
    Mono<PageImpl<ExpenseCategory>> getAll(int pageNo, int pageSize);

    Mono<ExpenseCategory> getById(Long id);

    Flux<ExpenseCategory> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(ExpenseCategory expenseCategory);

    Mono<ResponseEntity<ObjectNode>> update(ExpenseCategory expenseCategory);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
