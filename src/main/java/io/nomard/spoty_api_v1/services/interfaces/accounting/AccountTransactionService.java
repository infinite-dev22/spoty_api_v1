package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface AccountTransactionService {
    Page<AccountTransaction> getAll(int pageNo, int pageSize);

    ResponseEntity<ObjectNode> save(AccountTransaction accountTransaction);
}
