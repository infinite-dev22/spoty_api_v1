package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleTerm;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleTermServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sale_terms")
public class SaleTermController {

    @Autowired
    private SaleTermServiceImpl saleTermService;

    @GetMapping("/all")
    public List<SaleTerm> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                 @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleTermService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleTerm getById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleTermService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<SaleTerm> getByContains(@RequestBody SearchModel searchModel) {
        return saleTermService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody SaleTerm saleTerm) {
        return saleTermService.save(saleTerm);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody SaleTerm saleTerm) throws NotFoundException {
        return saleTermService.update(saleTerm);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return saleTermService.delete(findModel.getId());
    }
}
