package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.ExpenseCategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/expense_categories")
public class ExpenseCategoryController {
    @Autowired
    private ExpenseCategoryServiceImpl expenseCategoryService;


    @GetMapping("/all")
    public List<ExpenseCategory> getAll() {
        return expenseCategoryService.getAll();
    }

    @PostMapping("/single")
    public ExpenseCategory getById(@RequestBody Long id) throws NotFoundException {
        return expenseCategoryService.getById(id);
    }

    @PostMapping("/search")
    public List<ExpenseCategory> getByContains(@RequestBody String search) {
        return expenseCategoryService.getByContains(search);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody ExpenseCategory expenseCategory) {
        expenseCategory.setCreatedAt(new Date());
        return expenseCategoryService.save(expenseCategory);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody ExpenseCategory expenseCategory) {
        expenseCategory.setId(id);
        expenseCategory.setUpdatedAt(new Date());
        return expenseCategoryService.update(id, expenseCategory);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return expenseCategoryService.delete(id);
    }
}
