package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface ExpenseCategoryService {
    List<ExpenseCategory> getAll(int pageNo, int pageSize);

    ExpenseCategory getById(Long id) throws NotFoundException;

    List<ExpenseCategory> getByContains(String search);

    ResponseEntity<ObjectNode> save(ExpenseCategory expenseCategory);

    ResponseEntity<ObjectNode> update(ExpenseCategory expenseCategory) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
