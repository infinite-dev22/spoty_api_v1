package io.nomard.spoty_api_v1.controllers.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.accounting.ExpenseServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseServiceImpl expenseService;

    @GetMapping("/all")
    public Flux<PageImpl<Expense>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return expenseService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Expense> getById(@RequestBody FindModel findModel) {
        return expenseService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Expense> getByContains(@RequestBody SearchModel searchModel) {
        return expenseService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Expense expense) {
        expense.setCreatedAt(new Date());
        return expenseService.save(expense);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Expense expense) {
        return expenseService.update(expense);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return expenseService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return expenseService.deleteMultiple(idList);
    }
}
