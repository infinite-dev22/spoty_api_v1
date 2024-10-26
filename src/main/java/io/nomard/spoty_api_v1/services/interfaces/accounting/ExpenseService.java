package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ExpenseDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface ExpenseService {
    Page<ExpenseDTO> getAll(int pageNo, int pageSize);

    ExpenseDTO getById(Long id) throws NotFoundException;

    List<ExpenseDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(Expense expense);

    ResponseEntity<ObjectNode> update(Expense expense) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
