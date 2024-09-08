package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sales")
public class SaleController {
    @Autowired
    private SaleMasterServiceImpl saleMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    public Page<SaleMaster> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleMaster getById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<SaleMaster> getByContains(@RequestBody SearchModel searchModel) {
        return saleMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody SaleMaster saleMaster) throws NotFoundException {
        return saleMasterService.save(saleMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody SaleMaster saleMaster) throws NotFoundException {
        return saleMasterService.update(saleMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return saleMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return saleMasterService.deleteMultiple(idList);
    }
}
