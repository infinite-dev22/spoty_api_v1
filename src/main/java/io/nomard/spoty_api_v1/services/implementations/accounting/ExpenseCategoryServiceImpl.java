package io.nomard.spoty_api_v1.services.implementations.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.accounting.ExpenseCategoryRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.accounting.ExpenseCategoryService;
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
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {
    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public Mono<PageImpl<ExpenseCategory>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> expenseCategoryRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize)))
                .collectList()
                .zipWith(expenseCategoryRepo.count())
                .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2()));
    }

    @Override
    public Mono<ExpenseCategory> getById(Long id) {
        return expenseCategoryRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<ExpenseCategory> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> expenseCategoryRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(ExpenseCategory expenseCategory) {
        return authService.authUser().flatMap(user -> {
            expenseCategory.setTenant(user.getTenant());
            if (Objects.isNull(expenseCategory.getBranch())) {
                expenseCategory.setBranch(user.getBranch());
            }
            expenseCategory.setCreatedBy(user);
            expenseCategory.setCreatedAt(new Date());
            return expenseCategoryRepo.save(expenseCategory)
                    .thenReturn(spotyResponseImpl.created());
        }).onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(ExpenseCategory data) {
        return expenseCategoryRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Expense Category not found")))
                .flatMap(expenseCategory -> {
                    boolean updated = false;

                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        expenseCategory.setName(data.getName());
                        updated = true;
                    }

                    if (Objects.nonNull(data.getDescription()) && !"".equalsIgnoreCase(data.getDescription())) {
                        expenseCategory.setDescription(data.getDescription());
                        updated = true;
                    }

                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    expenseCategory.setUpdatedBy(user);
                                    expenseCategory.setUpdatedAt(new Date());
                                    return expenseCategoryRepo.save(expenseCategory)
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
        return expenseCategoryRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return expenseCategoryRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
