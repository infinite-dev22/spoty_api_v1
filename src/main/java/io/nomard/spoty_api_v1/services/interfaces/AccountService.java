package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Account;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface AccountService {
    List<Account> getAll(int pageNo, int pageSize);

    Account getById(Long id) throws NotFoundException;

    List<Account> getByContains(String search);

    ResponseEntity<ObjectNode> save(Account account);

    ResponseEntity<ObjectNode> update(Account account) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
