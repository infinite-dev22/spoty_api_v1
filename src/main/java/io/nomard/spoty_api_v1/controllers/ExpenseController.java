package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.ExpenseServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseServiceImpl expenseService;


    @GetMapping("/all")
    public List<Expense> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "20") Integer pageSize) {
        return expenseService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Expense getById(@RequestBody FindModel findModel) throws NotFoundException {
        return expenseService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<Expense> getByContains(@RequestBody SearchModel searchModel) {
        return expenseService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Expense expense) {
        expense.setCreatedAt(new Date());
        return expenseService.save(expense);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Expense expense) throws NotFoundException {
        return expenseService.update(expense);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return expenseService.delete(findModel.getId());
    }
}
