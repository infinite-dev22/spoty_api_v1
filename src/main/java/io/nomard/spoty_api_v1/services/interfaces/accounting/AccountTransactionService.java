package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountTransactionService {
    Flux<PageImpl<AccountTransaction>> getAll(int pageNo, int pageSize);

    Mono<ResponseEntity<ObjectNode>> save(AccountTransaction accountTransaction);
}
