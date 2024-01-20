package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
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
    public List<Currency> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        return currencyService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Currency getById(@RequestBody FindModel findModel) throws NotFoundException {
        return currencyService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<Currency> getByContains(@RequestBody SearchModel searchModel) {
        return currencyService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Currency currency) {
        currency.setCreatedAt(new Date());
        return currencyService.save(currency);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Currency currency) throws NotFoundException {
        return currencyService.update(currency);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return currencyService.delete(findModel.getId());
    }
}
