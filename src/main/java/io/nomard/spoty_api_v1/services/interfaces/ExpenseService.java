package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Expense;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExpenseService {
    List<Expense> getAll(int pageNo, int pageSize);

    Expense getById(Long id) throws NotFoundException;

    List<Expense> getByContains(String search);

    ResponseEntity<ObjectNode> save(Expense expense);

    ResponseEntity<ObjectNode> update(Expense expense) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
