package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.purchases.PurchaseDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.purchases.PurchaseMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseController {
    @Autowired
    private PurchaseMasterServiceImpl purchaseMasterService;

    @GetMapping("/all")
    public List<PurchaseMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return purchaseMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PurchaseMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return purchaseMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<PurchaseMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return purchaseMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody PurchaseMaster purchaseMaster) {
        return purchaseMasterService.save(purchaseMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody PurchaseMaster purchaseMaster) throws NotFoundException {
        return purchaseMasterService.update(purchaseMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return purchaseMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return purchaseMasterService.deleteMultiple(idList);
    }
}
