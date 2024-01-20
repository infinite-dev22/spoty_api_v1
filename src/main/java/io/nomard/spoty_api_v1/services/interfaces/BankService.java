package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Bank;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BankService {
    List<Bank> getAll(int pageNo, int pageSize);

    Bank getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Bank bank);

    ResponseEntity<ObjectNode> update(Bank bank) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
