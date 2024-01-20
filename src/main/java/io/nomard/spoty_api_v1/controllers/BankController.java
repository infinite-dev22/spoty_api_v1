package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Bank;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.BankServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("banks")
public class BankController {
    @Autowired
    private BankServiceImpl bankService;


    @GetMapping("/all")
    public List<Bank> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        return bankService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Bank getById(@RequestBody FindModel findModel) throws NotFoundException {
        return bankService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<Bank> getByContains(@RequestBody SearchModel searchModel) {
//        return bankService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Bank bank) {
        return bankService.save(bank);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Bank bank) throws NotFoundException {
        return bankService.update(bank);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return bankService.delete(findModel.getId());
    }
}
