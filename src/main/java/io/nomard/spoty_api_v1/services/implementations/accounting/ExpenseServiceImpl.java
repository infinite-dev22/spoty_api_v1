package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.ExpenseRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.ExpenseService;
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
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;

    @Override
    public Flux<PageImpl<Expense>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> expenseRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(expenseRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Expense> getById(Long id) {
        return expenseRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Expense> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> expenseRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Expense expense) {
        return authService.authUser().flatMap(user -> {
            expense.setTenant(user.getTenant());
            expense.setBranch(user.getBranch());
            expense.setCreatedBy(user);
            expense.setCreatedAt(new Date());

            return expenseRepo.save(expense).flatMap(savedExpense -> {
                if (!Objects.equals(savedExpense.getAmount(), 0d)) {
                    AccountTransaction expenseTransaction = new AccountTransaction();
                    expenseTransaction.setTenant(user.getTenant());
                    expenseTransaction.setTransactionDate(new Date());
                    expenseTransaction.setAccount(savedExpense.getAccount());
                    expenseTransaction.setAmount(savedExpense.getAmount());
                    expenseTransaction.setTransactionType("Expense");
                    expenseTransaction.setNote("Expense made");
                    expenseTransaction.setCreatedBy(user);
                    expenseTransaction.setCreatedAt(new Date());

                    return accountTransactionService.save(expenseTransaction)
                            .thenReturn(spotyResponseImpl.created());
                } else {
                    return Mono.just(spotyResponseImpl.created());
                }
            });
        }).onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Expense data) {
        return expenseRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Account not found")))
                .flatMap(expense -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getAccount()) && !Objects.equals(expense.getAccount(), data.getAccount())) {
                        expense.setAccount(data.getAccount());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getName()) && !Objects.equals(expense.getName(), data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        expense.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getRef()) && !Objects.equals(expense.getRef(), data.getRef()) && !"".equalsIgnoreCase(data.getRef())) {
                        expense.setRef(data.getRef());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getNote()) && !Objects.equals(expense.getNote(), data.getNote()) && !"".equalsIgnoreCase(data.getNote())) {
                        expense.setNote(data.getNote());
                        updated = true;
                    }

                    if (!Objects.equals(expense.getAmount(), data.getAmount()) && !Objects.equals(data.getAmount(), 0)) {
                        expense.setAmount(data.getAmount());
                        updated = true;
                    }

                    if (!Objects.equals(expense.getDate(), data.getDate()) && Objects.nonNull(data.getDate())) {
                        expense.setDate(data.getDate());
                        updated = true;
                    }

                    if (!Objects.equals(expense.getExpenseCategory(), data.getExpenseCategory()) && Objects.nonNull(data.getExpenseCategory())) {
                        expense.setExpenseCategory(data.getExpenseCategory());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    expense.setUpdatedBy(user);
                                    expense.setUpdatedAt(new Date());
                                    return expenseRepo.save(expense)
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
        return expenseRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return expenseRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
