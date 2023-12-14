package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExpenseCategoryService {
    List<ExpenseCategory> getAll();

    ExpenseCategory getById(Long id) throws NotFoundException;

    List<ExpenseCategory> getByContains(String search);

    ResponseEntity<ObjectNode> save(ExpenseCategory expenseCategory) throws NotFoundException;

    ResponseEntity<ObjectNode> update(Long id, ExpenseCategory expenseCategory);

    ResponseEntity<ObjectNode> delete(Long id);
}
