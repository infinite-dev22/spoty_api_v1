package io.nomard.spoty_api_v1.controllers.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.accounting.AccountServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("accounts")
public class AccountController {
    @Autowired
    private AccountServiceImpl bankService;

    @GetMapping("/all")
    public List<Account> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return bankService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Account getById(@RequestBody FindModel findModel) throws NotFoundException {
        return bankService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<Account> getByContains(@RequestBody SearchModel searchModel) {
        return bankService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Account account) {
        return bankService.save(account);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Account account) throws NotFoundException {
        return bankService.update(account);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return bankService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> ids) throws NotFoundException {
        return bankService.deleteMultiple(ids);
    }
}
