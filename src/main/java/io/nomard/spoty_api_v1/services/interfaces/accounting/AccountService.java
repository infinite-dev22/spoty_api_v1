package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface AccountService {
    Flux<PageImpl<Account>> getAll(int pageNo, int pageSize);

    Mono<Account> getById(Long id);

    Flux<Account> getByContains(String search);

    Mono<Account> getByContains(Tenant tenant, String search);

    Mono<ResponseEntity<ObjectNode>> save(Account account);

    Mono<ResponseEntity<ObjectNode>> update(Account account);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
