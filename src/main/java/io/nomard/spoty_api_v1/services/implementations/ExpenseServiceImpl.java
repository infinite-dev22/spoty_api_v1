package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ExpenseRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Expense> getAll() {
        return expenseRepo.findAll();
    }

    @Override
    public Expense getById(Long id) throws NotFoundException {
        Optional<Expense> expense = expenseRepo.findById(id);
        if (expense.isEmpty()) {
            throw new NotFoundException();
        }
        return expense.get();
    }

    @Override
    public List<Expense> getByContains(String search) {
        return expenseRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Expense expense) {
        try {
            expense.setCreatedBy(authService.authUser());
            expense.setCreatedAt(new Date());
            expenseRepo.saveAndFlush(expense);
            return spotyResponseImpl.created();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, Expense expense) {
        try {
            expense.setUpdatedBy(authService.authUser());
            expense.setUpdatedAt(new Date());
            expense.setId(id);
            expenseRepo.saveAndFlush(expense);
            return spotyResponseImpl.ok();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            expenseRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
