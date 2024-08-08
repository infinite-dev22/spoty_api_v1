package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.SaleTermAndConditionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("sale_terms_and_conditions")
public class SaleTermAndConditionController {
    @Autowired
    private SaleTermAndConditionServiceImpl saleTermAndConditionService;

    @GetMapping("/all")
    public Page<SaleTermAndCondition> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleTermAndConditionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleTermAndCondition getById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleTermAndConditionService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody SaleTermAndCondition saleTermAndCondition) {
        return saleTermAndConditionService.save(saleTermAndCondition);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody SaleTermAndCondition saleTermAndCondition) throws NotFoundException {
        return saleTermAndConditionService.update(saleTermAndCondition);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return saleTermAndConditionService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return saleTermAndConditionService.deleteMultiple(idList);
    }
}
