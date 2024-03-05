package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
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
    private PurchaseDetailServiceImpl purchaseDetailService;
    @Autowired
    private PurchaseMasterServiceImpl purchaseMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/masters")
    public List<PurchaseMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        return purchaseMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/master")
    public PurchaseMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return purchaseMasterService.getById(findModel.getId());
    }

//    @GetMapping("/masters/search")
//    public List<PurchaseMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
//        return purchaseMasterService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody PurchaseMaster purchaseMaster) {
        return purchaseMasterService.save(purchaseMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody PurchaseMaster purchaseMaster) throws NotFoundException {
        return purchaseMasterService.update(purchaseMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return purchaseMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return purchaseMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<PurchaseDetail> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        return purchaseDetailService.getAll(pageNo, pageSize);
    }

    @GetMapping("/detail")
    public PurchaseDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return purchaseDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<PurchaseDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return purchaseDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody PurchaseDetail purchaseDetail) {
        return purchaseDetailService.save(purchaseDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody PurchaseDetail purchaseDetail) throws NotFoundException {
        return purchaseDetailService.update(purchaseDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return purchaseDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody List<Long> idList) throws NotFoundException {
        return purchaseDetailService.deleteMultiple(idList);
    }
}
