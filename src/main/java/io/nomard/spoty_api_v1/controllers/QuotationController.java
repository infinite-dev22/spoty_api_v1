package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationDetail;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.quotations.QuotationDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.quotations.QuotationMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quotations")
public class QuotationController {
    @Autowired
    private QuotationDetailServiceImpl quotationDetailService;
    @Autowired
    private QuotationMasterServiceImpl quotationMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/masters")
    public List<QuotationMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "50") Integer pageSize) {
        return quotationMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/master")
    public QuotationMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return quotationMasterService.getById(findModel.getId());
    }

    @GetMapping("/masters/search")
    public List<QuotationMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return quotationMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody QuotationMaster quotationMaster) {
        return quotationMasterService.save(quotationMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody QuotationMaster quotationMaster) throws NotFoundException {
        return quotationMasterService.update(quotationMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return quotationMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return quotationMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<QuotationDetail> getAllDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                               @RequestParam(defaultValue = "50") Integer pageSize) {
        return quotationDetailService.getAll(pageNo, pageSize);
    }

    @GetMapping("/detail")
    public QuotationDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return quotationDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<QuotationDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return quotationDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody QuotationDetail quotationDetail) {
        return quotationDetailService.save(quotationDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody QuotationDetail quotationDetail) throws NotFoundException {
        return quotationDetailService.update(quotationDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return quotationDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody List<Long> idList) throws NotFoundException {
        return quotationDetailService.deleteMultiple(idList);
    }
}
