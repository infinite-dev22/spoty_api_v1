package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
import io.nomard.spoty_api_v1.repositories.accounting.AccountTransactionRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;

@Service
public class AccountTransactionServiceImpl implements AccountTransactionService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private AccountTransactionRepository accountTransactionRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<AccountTransaction>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> accountTransactionRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(accountTransactionRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(AccountTransaction accountTransaction) {
        return authService.authUser()
                .flatMap(user -> {
                    accountTransaction.setTenant(user.getTenant());
                    return accountRepo.findById(accountTransaction.getAccount().getId())
                            .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
                            .flatMap(account -> {
                                if (Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "deposit")
                                        || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "sale")
                                        || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "purchase returns")) {
                                    accountTransaction.setCredit(accountTransaction.getAmount());
                                    account.setCredit(accountTransaction.getAmount());
                                    account.setBalance(account.getBalance() + accountTransaction.getAmount());
                                }
                                if (Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "expense")
                                        || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "payroll")
                                        || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "sale returns")
                                        || Objects.equals(accountTransaction.getTransactionType().toLowerCase(), "purchase")) {
                                    accountTransaction.setDebit(accountTransaction.getAmount());
                                    account.setDebit(accountTransaction.getAmount());
                                    account.setBalance(account.getBalance() - accountTransaction.getAmount());
                                }
                                account.setUpdatedAt(new Date());
                                return authService.authUser()
                                        .flatMap(authUser -> {
                                            account.setUpdatedBy(authUser);
                                            return accountRepo.save(account)
                                                    .then(authService.authUser()
                                                            .flatMap(authUserAgain -> {
                                                                accountTransaction.setCreatedBy(authUserAgain);
                                                                accountTransaction.setCreatedAt(new Date());
                                                                return accountTransactionRepo.save(accountTransaction)
                                                                        .thenReturn(spotyResponseImpl.created());
                                                            }));
                                        });
                            });
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
