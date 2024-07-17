package io.nomard.spoty_api_v1.controllers.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    private final String BASE_URL = "accounts";
    private final String ACCOUNTS_URL = "accounts";
    private final String ACCOUNT_URL = "accounts";
    private final String SEARCH_ACCOUNTS_URL = "accounts";
    private final String SAVE_ACCOUNT_URL = "accounts";
    private final String UPDATE_ACCOUNT_URL = "accounts";
    private final String DELETE_ACCOUNT_URL = "accounts";
    private final String DELETE_ACCOUNTS_URL = "accounts";

    @GetMapping("/all")
    public Flux<PageImpl<Account>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        var client = WebClient.create(ACCOUNTS_URL);
        return accountService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Account> getById(@RequestBody FindModel findModel) {
        return accountService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Account> getByContains(@RequestBody SearchModel searchModel) {
        return accountService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Account account) {
        return accountService.save(account);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Account account) {
        return accountService.update(account);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return accountService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> ids) {
        return accountService.deleteMultiple(ids);
    }

    @GetMapping("/transactions")
    public Flux<PageImpl<AccountTransaction>> getTransactions(@RequestParam(defaultValue = "0") Integer pageNo,
                                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return accountTransactionService.getAll(pageNo, pageSize);
    }
}
