package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.QuotationDTO;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.quotations.QuotationServiceImpl;
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
    private QuotationServiceImpl quotationService;

    @GetMapping("/all")
    public Page<QuotationDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "50") Integer pageSize) {
        return quotationService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public QuotationDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return quotationService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<QuotationDTO> getByContains(@RequestBody SearchModel searchModel) {
        return quotationService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody QuotationMaster quotation) throws NotFoundException {
        return quotationService.save(quotation);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody QuotationMaster quotation) throws NotFoundException {
        return quotationService.update(quotation);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return quotationService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return quotationService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return quotationService.deleteMultiple(idList);
    }
}
