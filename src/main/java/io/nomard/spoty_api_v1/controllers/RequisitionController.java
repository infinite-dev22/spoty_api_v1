package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionDetail;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
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
    private RequisitionDetailServiceImpl requisitionDetailService;
    @Autowired
    private RequisitionMasterServiceImpl requisitionMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/masters")
    public List<RequisitionMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                 @RequestParam(defaultValue = "50") Integer pageSize) {
        return requisitionMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/master")
    public RequisitionMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return requisitionMasterService.getById(findModel.getId());
    }

//    @GetMapping("/masters/search")
//    public List<RequisitionMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
//        return requisitionMasterService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody RequisitionMaster requisitionMaster) {
        return requisitionMasterService.save(requisitionMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody RequisitionMaster requisitionMaster) throws NotFoundException {
        return requisitionMasterService.update(requisitionMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return requisitionMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return requisitionMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<RequisitionDetail> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                                 @RequestParam(defaultValue = "50") Integer pageSize) {
        return requisitionDetailService.getAll(pageNo, pageSize);
    }

    @GetMapping("/detail")
    public RequisitionDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return requisitionDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<RequisitionDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return requisitionDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody RequisitionDetail requisitionDetail) {
        return requisitionDetailService.save(requisitionDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody RequisitionDetail requisitionDetail) throws NotFoundException {
        return requisitionDetailService.update(requisitionDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return requisitionDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody List<Long> idList) throws NotFoundException {
        return requisitionDetailService.deleteMultiple(idList);
    }
}
