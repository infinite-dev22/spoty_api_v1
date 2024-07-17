package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.AccountRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Flux<PageImpl<Account>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> accountRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(accountRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Account> getById(Long id) {
        return accountRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Account> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> accountRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    public Mono<Account> getByContains(Tenant tenant, String search) {
        return accountRepo.findByTenantAndAccountNameContainingIgnoreCase(tenant, search);
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Account account) {
        return authService.authUser()
                .flatMap(authUser -> {
                    account.setTenant(authUser.getTenant());
                    Double amount = account.getBalance();
                    account.setBalance(0d);
                    account.setCreatedBy(authUser);
                    account.setCreatedAt(new Date());

                    return accountRepo.save(account).flatMap(savedAccount -> {
                                if (Objects.nonNull(amount) && !Objects.equals(amount, 0d)) {
                                    AccountTransaction accountTransaction = new AccountTransaction();
                                    accountTransaction.setTenant(authUser.getTenant());
                                    accountTransaction.setTransactionDate(new Date());
                                    accountTransaction.setAccount(savedAccount);
                                    accountTransaction.setAmount(amount);
                                    accountTransaction.setTransactionType("Deposit");
                                    accountTransaction.setNote("Initial deposit");
                                    accountTransaction.setCreatedBy(authUser);
                                    accountTransaction.setCreatedAt(new Date());

                                    return accountTransactionService.save(accountTransaction)
                                            .thenReturn(spotyResponseImpl.created());
                                } else {
                                    return Mono.just(spotyResponseImpl.created());
                                }
                            })
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Account data) {
        return accountRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
                .flatMap(account -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getAccountName()) && !Objects.equals(account.getAccountName(), data.getAccountName()) && !"".equalsIgnoreCase(data.getAccountName())) {
                        account.setAccountName(data.getAccountName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getAccountNumber()) && !Objects.equals(account.getAccountNumber(), data.getAccountNumber()) && !"".equalsIgnoreCase(data.getAccountNumber())) {
                        account.setAccountNumber(data.getAccountNumber());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getCredit()) && !Objects.equals(account.getCredit(), data.getCredit()) && !Objects.equals(data.getCredit(), 0d)) {
                        account.setCredit(data.getCredit());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDebit()) && !Objects.equals(account.getDebit(), data.getDebit()) && !Objects.equals(data.getDebit(), 0d)) {
                        account.setDebit(data.getDebit());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !Objects.equals(account.getDescription(), data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        account.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    account.setUpdatedBy(user);
                                    account.setUpdatedAt(new Date());
                                    return accountRepo.save(account)
                                            .thenReturn(spotyResponseImpl.ok());
                                });
                    } else {
                        return Mono.just(spotyResponseImpl.ok());
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return accountRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return accountRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
