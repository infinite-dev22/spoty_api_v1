package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.CurrencyServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    @Autowired
    private CurrencyServiceImpl currencyService;


    @GetMapping("/all")
    public List<Currency> getAll() {
        return currencyService.getAll();
    }

    @PostMapping("/single")
    public Currency getById(@RequestBody Long id) throws NotFoundException {
        return currencyService.getById(id);
    }

    @PostMapping("/search")
    public List<Currency> getByContains(@RequestBody String search) {
        return currencyService.getByContains(search);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Currency currency) {
        currency.setCreatedAt(new Date());
        return currencyService.save(currency);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody Currency currency) {
        currency.setId(id);
        currency.setUpdatedAt(new Date());
        return currencyService.update(id, currency);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return currencyService.delete(id);
    }
}
