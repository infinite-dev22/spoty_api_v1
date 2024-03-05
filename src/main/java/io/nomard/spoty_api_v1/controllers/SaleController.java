package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
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
    @GetMapping("/masters")
    public List<SaleMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/master")
    public SaleMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleMasterService.getById(findModel.getId());
    }

//    @GetMapping("/masters/search")
//    public List<SaleMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
//        return saleMasterService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody SaleMaster saleMaster) {
        return saleMasterService.save(saleMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody SaleMaster saleMaster) throws NotFoundException {
        return saleMasterService.update(saleMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return saleMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return saleMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<SaleDetail> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleDetailService.getAll(pageNo, pageSize);
    }

    @GetMapping("/detail")
    public SaleDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<SaleDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return saleDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody SaleDetail saleDetail) {
        return saleDetailService.save(saleDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody SaleDetail saleDetail) throws NotFoundException {
        return saleDetailService.update(saleDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return saleDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody List<Long> idList) throws NotFoundException {
        return saleDetailService.deleteMultiple(idList);
    }
}
