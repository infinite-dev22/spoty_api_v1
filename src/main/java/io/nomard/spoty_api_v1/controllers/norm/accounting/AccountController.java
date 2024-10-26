package io.nomard.spoty_api_v1.controllers.norm.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.AccountDTO;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.AccountTransactionDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountTransactionServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AccountTransactionServiceImpl accountTransactionService;

    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<AccountDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return accountService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public AccountDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return accountService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<AccountDTO> getByContains(@RequestBody SearchModel searchModel) {
        return accountService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Account account) {
        return accountService.save(account);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Account account) throws NotFoundException {
        return accountService.update(account);
    }

    @PutMapping("/deposit")
    public ResponseEntity<ObjectNode> deposit(@Valid @RequestBody Account account) throws NotFoundException {
        return accountService.deposit(account);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return accountService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> ids) throws NotFoundException {
        return accountService.deleteMultiple(ids);
    }

    @GetMapping("/transactions")
    public Page<AccountTransactionDTO> getTransactions(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return accountTransactionService.getAll(pageNo, pageSize);
    }
}
