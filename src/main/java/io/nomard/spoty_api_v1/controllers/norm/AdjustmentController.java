package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.adjustments.AdjustmentServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("adjustments")
public class AdjustmentController {
    @Autowired
    private AdjustmentServiceImpl adjustmentService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<AdjustmentMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return adjustmentService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public AdjustmentMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return adjustmentService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<AdjustmentMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return adjustmentService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody AdjustmentMaster adjustment) {
        return adjustmentService.save(adjustment);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody AdjustmentMaster adjustment) throws NotFoundException {
        return adjustmentService.update(adjustment);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return adjustmentService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return adjustmentService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return adjustmentService.deleteMultiple(idList);
    }
}
