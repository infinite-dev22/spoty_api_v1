package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountTransactionService {
    List<AccountTransaction> getAll(int pageNo, int pageSize);

    ResponseEntity<ObjectNode> save(AccountTransaction accountTransaction);
}