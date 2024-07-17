package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.quotations.QuotationMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("quotations")
public class QuotationController {
    @Autowired
    private QuotationMasterServiceImpl quotationMasterService;

    @GetMapping("/all")
    public Flux<PageImpl<QuotationMaster>> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                         @RequestParam(defaultValue = "50") Integer pageSize) {
        return quotationMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<QuotationMaster> getMastersById(@RequestBody FindModel findModel) {
        return quotationMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<QuotationMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return quotationMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> saveMaster(@Valid @RequestBody QuotationMaster quotationMaster) {
        return quotationMasterService.save(quotationMaster);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> updateMaster(@Valid @RequestBody QuotationMaster quotationMaster) {
        return quotationMasterService.update(quotationMaster);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> deleteMaster(@RequestBody FindModel findModel) {
        return quotationMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMasters(@RequestBody List<Long> idList) {
        return quotationMasterService.deleteMultiple(idList);
    }
}
