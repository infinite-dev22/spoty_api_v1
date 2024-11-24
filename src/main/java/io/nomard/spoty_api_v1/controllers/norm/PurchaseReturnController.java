package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PurchaseReturnDTO;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.returns.purchase_returns.PurchaseReturnServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases/returns")
public class PurchaseReturnController {
    @Autowired
    private PurchaseReturnServiceImpl purchaseReturnService;

    @GetMapping("/all")
    public Page<PurchaseReturnDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return purchaseReturnService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PurchaseReturnDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return purchaseReturnService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<PurchaseReturnDTO> getByContains(@RequestBody SearchModel searchModel) {
        return purchaseReturnService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody PurchaseReturnMaster purchaseReturn) throws NotFoundException {
        return purchaseReturnService.save(purchaseReturn);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody PurchaseReturnMaster purchaseReturn) throws NotFoundException {
        return purchaseReturnService.update(purchaseReturn);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return purchaseReturnService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return purchaseReturnService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return purchaseReturnService.deleteMultiple(idList);
    }
}
