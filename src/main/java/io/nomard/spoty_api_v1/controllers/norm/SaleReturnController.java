package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.SaleReturnDTO;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.returns.sale_returns.SaleReturnServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sales/returns")
public class SaleReturnController {
    @Autowired
    private SaleReturnServiceImpl saleReturnService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    public Page<SaleReturnDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleReturnService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleReturnDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleReturnService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<SaleReturnDTO> getByContains(@RequestBody SearchModel searchModel) {
        return saleReturnService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody SaleReturnMaster saleReturn) throws NotFoundException {
        return saleReturnService.save(saleReturn);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody SaleReturnMaster saleReturn) throws NotFoundException {
        return saleReturnService.update(saleReturn);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return saleReturnService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return saleReturnService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return saleReturnService.deleteMultiple(idList);
    }
}
