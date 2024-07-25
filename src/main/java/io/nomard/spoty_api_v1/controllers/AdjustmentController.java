package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.adjustments.AdjustmentMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("adjustments")
public class AdjustmentController {
    @Autowired
    private AdjustmentMasterServiceImpl adjustmentMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    public List<AdjustmentMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return adjustmentMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public AdjustmentMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return adjustmentMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<AdjustmentMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return adjustmentMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody AdjustmentMaster adjustmentMaster) {
        return adjustmentMasterService.save(adjustmentMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody AdjustmentMaster adjustmentMaster) throws NotFoundException {
        return adjustmentMasterService.update(adjustmentMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return adjustmentMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return adjustmentMasterService.deleteMultiple(idList);
    }
}
