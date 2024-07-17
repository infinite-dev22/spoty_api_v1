package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.requisitions.RequisitionMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("requisitions")
public class RequisitionController {
    @Autowired
    private RequisitionMasterServiceImpl requisitionMasterService;

    @GetMapping("/all")
    public Flux<PageImpl<RequisitionMaster>> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                           @RequestParam(defaultValue = "50") Integer pageSize) {
        return requisitionMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<RequisitionMaster> getMastersById(@RequestBody FindModel findModel) {
        return requisitionMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<RequisitionMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return requisitionMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> saveMaster(@Valid @RequestBody RequisitionMaster requisitionMaster) {
        return requisitionMasterService.save(requisitionMaster);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> updateMaster(@Valid @RequestBody RequisitionMaster requisitionMaster) {
        return requisitionMasterService.update(requisitionMaster);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> deleteMaster(@RequestBody FindModel findModel) {
        return requisitionMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMasters(@RequestBody List<Long> idList) {
        return requisitionMasterService.deleteMultiple(idList);
    }
}
