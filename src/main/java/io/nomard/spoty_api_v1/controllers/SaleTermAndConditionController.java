package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.SaleTermAndConditionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("sale_terms_and_conditions")
public class SaleTermAndConditionController {
    @Autowired
    private SaleTermAndConditionServiceImpl saleTermAndConditionService;

    @GetMapping("/all")
    public Flux<PageImpl<SaleTermAndCondition>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleTermAndConditionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<SaleTermAndCondition> getById(@RequestBody FindModel findModel) {
        return saleTermAndConditionService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody SaleTermAndCondition saleTermAndCondition) {
        return saleTermAndConditionService.save(saleTermAndCondition);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody SaleTermAndCondition saleTermAndCondition) {
        return saleTermAndConditionService.update(saleTermAndCondition);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return saleTermAndConditionService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return saleTermAndConditionService.deleteMultiple(idList);
    }
}
