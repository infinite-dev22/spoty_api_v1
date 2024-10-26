package io.nomard.spoty_api_v1.services.interfaces.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.AccountDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface AccountService {
    Page<AccountDTO> getAll(int pageNo, int pageSize);

    AccountDTO getById(Long id) throws NotFoundException;

    List<AccountDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(Account account);

    ResponseEntity<ObjectNode> update(Account account) throws NotFoundException;

    ResponseEntity<ObjectNode> deposit(Account account) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
