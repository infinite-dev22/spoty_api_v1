package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.requisitions.RequisitionDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.requisitions.RequisitionMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("requisitions")
public class RequisitionController {
    @Autowired
    private RequisitionMasterServiceImpl requisitionMasterService;

    @GetMapping("/all")
    public List<RequisitionMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                 @RequestParam(defaultValue = "50") Integer pageSize) {
        return requisitionMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public RequisitionMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return requisitionMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<RequisitionMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return requisitionMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody RequisitionMaster requisitionMaster) {
        return requisitionMasterService.save(requisitionMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody RequisitionMaster requisitionMaster) throws NotFoundException {
        return requisitionMasterService.update(requisitionMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return requisitionMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return requisitionMasterService.deleteMultiple(idList);
    }
}
