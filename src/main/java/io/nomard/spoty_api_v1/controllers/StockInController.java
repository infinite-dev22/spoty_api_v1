package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.stock_ins.StockInMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stock_ins")
public class StockInController {
    @Autowired
    private StockInMasterServiceImpl stockInMasterService;

    @GetMapping("/all")
    public Page<StockInMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "50") Integer pageSize) {
        return stockInMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public StockInMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return stockInMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<StockInMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return stockInMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody StockInMaster stockInMaster) {
        return stockInMasterService.save(stockInMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody StockInMaster stockInMaster) throws NotFoundException {
        return stockInMasterService.update(stockInMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return stockInMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return stockInMasterService.deleteMultiple(idList);
    }
}
