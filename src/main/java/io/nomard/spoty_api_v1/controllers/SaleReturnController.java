package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.returns.sale_returns.SaleReturnMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sales/returns")
public class SaleReturnController {
    @Autowired
    private SaleReturnMasterServiceImpl saleReturnMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    public List<SaleReturnMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleReturnMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleReturnMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleReturnMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<SaleReturnMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return saleReturnMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody SaleReturnMaster saleReturnMaster) {
        return saleReturnMasterService.save(saleReturnMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody SaleReturnMaster saleReturnMaster) throws NotFoundException {
        return saleReturnMasterService.update(saleReturnMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return saleReturnMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return saleReturnMasterService.deleteMultiple(idList);
    }
}
