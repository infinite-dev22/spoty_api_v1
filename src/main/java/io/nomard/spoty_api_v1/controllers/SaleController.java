package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sales")
public class SaleController {
    @Autowired
    private SaleDetailServiceImpl saleDetailService;
    @Autowired
    private SaleMasterServiceImpl saleMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    public List<SaleMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<SaleMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return saleMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody SaleMaster saleMaster) {
        return saleMasterService.save(saleMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody SaleMaster saleMaster) throws NotFoundException {
        return saleMasterService.update(saleMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return saleMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return saleMasterService.deleteMultiple(idList);
    }
}
