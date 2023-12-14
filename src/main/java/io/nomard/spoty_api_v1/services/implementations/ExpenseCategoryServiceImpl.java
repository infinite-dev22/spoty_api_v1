package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.ExpenseCategoryRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {
    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<ExpenseCategory> getAll() {
        return expenseCategoryRepo.findAll();
    }

    @Override
    public ExpenseCategory getById(Long id) throws NotFoundException {
        Optional<ExpenseCategory> expenseCategory = expenseCategoryRepo.findById(id);
        if (expenseCategory.isEmpty()) {
            throw new NotFoundException();
        }
        return expenseCategory.get();
    }

    @Override
    public List<ExpenseCategory> getByContains(String search) {
        return expenseCategoryRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(ExpenseCategory expenseCategory) {
        try {
            expenseCategory.setCreatedBy(authService.authUser());
            expenseCategory.setCreatedAt(new Date());
            expenseCategoryRepo.saveAndFlush(expenseCategory);
            return spotyResponseImpl.created();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, ExpenseCategory expenseCategory) {
        try {
            expenseCategory.setUpdatedBy(authService.authUser());
            expenseCategory.setUpdatedAt(new Date());
            expenseCategory.setId(id);
            expenseCategoryRepo.saveAndFlush(expenseCategory);
            return spotyResponseImpl.ok();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            expenseCategoryRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
