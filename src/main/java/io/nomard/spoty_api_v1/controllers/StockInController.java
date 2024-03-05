package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.stock_ins.StockInDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.stock_ins.StockInMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stock_ins")
public class StockInController {
    @Autowired
    private StockInDetailServiceImpl stockInDetailService;
    @Autowired
    private StockInMasterServiceImpl stockInMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/masters")
    public List<StockInMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "20") Integer pageSize) {
        return stockInMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/master")
    public StockInMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return stockInMasterService.getById(findModel.getId());
    }

//    @GetMapping("/masters/search")
//    public List<StockInMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
//        return stockInMasterService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody StockInMaster stockInMaster) {
        return stockInMasterService.save(stockInMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody StockInMaster stockInMaster) throws NotFoundException {
        return stockInMasterService.update(stockInMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return stockInMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return stockInMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<StockInDetail> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "20") Integer pageSize) {
        return stockInDetailService.getAll(pageNo, pageSize);
    }

    @GetMapping("/detail")
    public StockInDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return stockInDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<StockInDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return stockInDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody StockInDetail stockInDetail) {
        return stockInDetailService.save(stockInDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody StockInDetail stockInDetail) throws NotFoundException {
        return stockInDetailService.update(stockInDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return stockInDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody List<Long> idList) throws NotFoundException {
        return stockInDetailService.deleteMultiple(idList);
    }
}
