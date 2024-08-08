package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.returns.purchase_returns.PurchaseReturnMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases/returns")
public class PurchaseReturnController {
    @Autowired
    private PurchaseReturnMasterServiceImpl purchaseReturnMasterService;

    @GetMapping("/all")
    public Page<PurchaseReturnMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return purchaseReturnMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PurchaseReturnMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return purchaseReturnMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<PurchaseReturnMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return purchaseReturnMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody PurchaseReturnMaster purchaseReturnMaster) {
        return purchaseReturnMasterService.save(purchaseReturnMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody PurchaseReturnMaster purchaseReturnMaster) throws NotFoundException {
        return purchaseReturnMasterService.update(purchaseReturnMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return purchaseReturnMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return purchaseReturnMasterService.deleteMultiple(idList);
    }
}
