package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ExpenseCategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/expense/categories")
public class ExpenseCategoryController {
    @Autowired
    private ExpenseCategoryServiceImpl expenseCategoryService;

    @GetMapping("/all")
    public List<ExpenseCategory> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "50") Integer pageSize) {
        return expenseCategoryService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public ExpenseCategory getById(@RequestBody FindModel findModel) throws NotFoundException {
        return expenseCategoryService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<ExpenseCategory> getByContains(@RequestBody SearchModel searchModel) {
        return expenseCategoryService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody ExpenseCategory expenseCategory) {
        expenseCategory.setCreatedAt(new Date());
        return expenseCategoryService.save(expenseCategory);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody ExpenseCategory expenseCategory) throws NotFoundException {
        return expenseCategoryService.update(expenseCategory);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return expenseCategoryService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return expenseCategoryService.deleteMultiple(idList);
    }
}
