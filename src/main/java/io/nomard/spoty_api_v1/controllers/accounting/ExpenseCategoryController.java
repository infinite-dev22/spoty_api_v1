package io.nomard.spoty_api_v1.controllers.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.accounting.ExpenseCategoryServiceImpl;
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
@RequestMapping("/expense/categories")
public class ExpenseCategoryController {
    @Autowired
    private ExpenseCategoryServiceImpl expenseCategoryService;

    @GetMapping("/all")
    public Mono<PageImpl<ExpenseCategory>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "50") Integer pageSize) {
        return expenseCategoryService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<ExpenseCategory> getById(@RequestBody FindModel findModel) {
        return expenseCategoryService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<ExpenseCategory> getByContains(@RequestBody SearchModel searchModel) {
        return expenseCategoryService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody ExpenseCategory expenseCategory) {
        expenseCategory.setCreatedAt(new Date());
        return expenseCategoryService.save(expenseCategory);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody ExpenseCategory expenseCategory) {
        return expenseCategoryService.update(expenseCategory);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return expenseCategoryService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return expenseCategoryService.deleteMultiple(idList);
    }
}
