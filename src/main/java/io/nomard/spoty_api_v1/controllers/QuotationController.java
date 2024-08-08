package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.quotations.QuotationMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quotations")
public class QuotationController {
    @Autowired
    private QuotationMasterServiceImpl quotationMasterService;

    @GetMapping("/all")
    public Page<QuotationMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "50") Integer pageSize) {
        return quotationMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public QuotationMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return quotationMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<QuotationMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return quotationMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody QuotationMaster quotationMaster) {
        return quotationMasterService.save(quotationMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody QuotationMaster quotationMaster) throws NotFoundException {
        return quotationMasterService.update(quotationMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return quotationMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return quotationMasterService.deleteMultiple(idList);
    }
}
