package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.adjustments.AdjustmentDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.adjustments.AdjustmentMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("adjustments")
public class AdjustmentController {
    @Autowired
    private AdjustmentDetailServiceImpl adjustmentDetailService;
    @Autowired
    private AdjustmentMasterServiceImpl adjustmentMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/masters")
    public List<AdjustmentMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "20") Integer pageSize) {
        return adjustmentMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/master")
    public AdjustmentMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return adjustmentMasterService.getById(findModel.getId());
    }

//    @GetMapping("/masters/search")
//    public List<AdjustmentMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
//        return adjustmentMasterService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody AdjustmentMaster adjustmentMaster) {
        return adjustmentMasterService.save(adjustmentMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody AdjustmentMaster adjustmentMaster) throws NotFoundException {
        return adjustmentMasterService.update(adjustmentMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return adjustmentMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody ArrayList<Long> idList) {
        return adjustmentMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<AdjustmentDetail> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "20") Integer pageSize) {
        return adjustmentDetailService.getAll(pageNo, pageSize);
    }

    @GetMapping("/detail")
    public AdjustmentDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return adjustmentDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<AdjustmentDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return adjustmentDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody AdjustmentDetail adjustmentDetail) {
        return adjustmentDetailService.save(adjustmentDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody AdjustmentDetail adjustmentDetail) throws NotFoundException {
        return adjustmentDetailService.update(adjustmentDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return adjustmentDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody ArrayList<Long> idList) throws NotFoundException {
        return adjustmentDetailService.deleteMultiple(idList);
    }
}
