package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.purchases.PurchaseMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseController {
    @Autowired
    private PurchaseMasterServiceImpl purchaseMasterService;

    @GetMapping("/all")
    public Flux<PageImpl<PurchaseMaster>> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                        @RequestParam(defaultValue = "50") Integer pageSize) {
        return purchaseMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<PurchaseMaster> getMastersById(@RequestBody FindModel findModel) {
        return purchaseMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<PurchaseMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return purchaseMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> saveMaster(@Valid @RequestBody PurchaseMaster purchaseMaster) {
        return purchaseMasterService.save(purchaseMaster);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> updateMaster(@Valid @RequestBody PurchaseMaster purchaseMaster) {
        return purchaseMasterService.update(purchaseMaster);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> deleteMaster(@RequestBody FindModel findModel) {
        return purchaseMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMasters(@RequestBody List<Long> idList) {
        return purchaseMasterService.deleteMultiple(idList);
    }
}
