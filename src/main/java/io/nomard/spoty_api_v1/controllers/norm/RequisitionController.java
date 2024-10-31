package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.RequisitionDTO;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.requisitions.RequisitionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("requisitions")
public class RequisitionController {
    @Autowired
    private RequisitionServiceImpl requisitionService;

    @GetMapping("/all")
    public Page<RequisitionDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return requisitionService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public RequisitionDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return requisitionService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<RequisitionDTO> getByContains(@RequestBody SearchModel searchModel) {
        return requisitionService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody RequisitionMaster requisition) {
        return requisitionService.save(requisition);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody RequisitionMaster requisition) throws NotFoundException {
        return requisitionService.update(requisition);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return requisitionService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return requisitionService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return requisitionService.deleteMultiple(idList);
    }
}
