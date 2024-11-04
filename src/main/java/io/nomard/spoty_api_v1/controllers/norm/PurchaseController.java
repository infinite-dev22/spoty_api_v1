package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PurchaseDTO;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.purchases.PurchaseServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("purchases")
public class PurchaseController {
    @Autowired
    private PurchaseServiceImpl purchaseService;

    @GetMapping("/all")
    public Page<PurchaseDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return purchaseService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PurchaseDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return purchaseService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<PurchaseDTO> getByContains(@RequestBody SearchModel searchModel) {
        return purchaseService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody PurchaseMaster purchase) throws NotFoundException {
        return purchaseService.save(purchase);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody PurchaseMaster purchase) throws NotFoundException {
        return purchaseService.update(purchase);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return purchaseService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return purchaseService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return purchaseService.deleteMultiple(idList);
    }
}
