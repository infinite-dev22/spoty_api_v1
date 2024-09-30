package io.nomard.spoty_api_v1.controllers.client.api;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.sales.SaleServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sales")
public class SaleController {
    @Autowired
    private SaleServiceImpl saleService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<SaleMaster> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return saleService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SaleMaster getById(@RequestBody FindModel findModel) throws NotFoundException {
        return saleService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<SaleMaster> getByContains(@RequestBody SearchModel searchModel) {
        return saleService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody SaleMaster sale) throws NotFoundException {
        return saleService.save(sale);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody SaleMaster sale) throws NotFoundException {
        return saleService.update(sale);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return saleService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return saleService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return saleService.deleteMultiple(idList);
    }
}
