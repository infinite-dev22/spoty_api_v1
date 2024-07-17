package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.stock_ins.StockInMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("stock_ins")
public class StockInController {
    @Autowired
    private StockInMasterServiceImpl stockInMasterService;

    @GetMapping("/all")
    public Flux<PageImpl<StockInMaster>> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return stockInMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<StockInMaster> getMastersById(@RequestBody FindModel findModel) {
        return stockInMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<StockInMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return stockInMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> saveMaster(@Valid @RequestBody StockInMaster stockInMaster) {
        return stockInMasterService.save(stockInMaster);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> updateMaster(@Valid @RequestBody StockInMaster stockInMaster) {
        return stockInMasterService.update(stockInMaster);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> deleteMaster(@RequestBody FindModel findModel) {
        return stockInMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMasters(@RequestBody List<Long> idList) {
        return stockInMasterService.deleteMultiple(idList);
    }
}
