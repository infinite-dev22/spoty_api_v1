package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Currency;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.CurrencyServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    @Autowired
    private CurrencyServiceImpl currencyService;

    @GetMapping("/all")
    public Flux<PageImpl<Currency>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "50") Integer pageSize) {
        return currencyService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Currency> getById(@RequestBody FindModel findModel) {
        return currencyService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Currency> getByContains(@RequestBody SearchModel searchModel) {
        return currencyService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Currency currency) {
        currency.setCreatedAt(new Date());
        return currencyService.save(currency);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Currency currency) {
        return currencyService.update(currency);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return currencyService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody ArrayList<Long> idList) {
        return currencyService.deleteMultiple(idList);
    }
}
