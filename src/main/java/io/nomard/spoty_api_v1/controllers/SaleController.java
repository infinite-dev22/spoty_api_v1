package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("sales")
public class SaleController {
    @Autowired
    private SaleMasterServiceImpl saleMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    public Flux<PageImpl<SaleMaster>> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<SaleMaster> getMastersById(@RequestBody FindModel findModel) {
        return saleMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<SaleMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return saleMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> saveMaster(@Valid @RequestBody SaleMaster saleMaster) {
        return saleMasterService.save(saleMaster);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> updateMaster(@Valid @RequestBody SaleMaster saleMaster) {
        return saleMasterService.update(saleMaster);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> deleteMaster(@RequestBody FindModel findModel) {
        return saleMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMasters(@RequestBody List<Long> idList) {
        return saleMasterService.deleteMultiple(idList);
    }
}
