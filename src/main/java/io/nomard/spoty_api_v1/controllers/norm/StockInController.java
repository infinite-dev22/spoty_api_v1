package io.nomard.spoty_api_v1.controllers.norm;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.StockInDTO;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.stock_ins.StockInServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("stock_ins")
public class StockInController {
    @Autowired
    private StockInServiceImpl stockInService;

    @GetMapping("/all")
    public Page<StockInDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return stockInService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public StockInDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return stockInService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<StockInDTO> getByContains(@RequestBody SearchModel searchModel) {
        return stockInService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody StockInMaster stockIn) {
        return stockInService.save(stockIn);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody StockInMaster stockIn) throws NotFoundException {
        return stockInService.update(stockIn);
    }

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return stockInService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return stockInService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody List<Long> idList) {
        return stockInService.deleteMultiple(idList);
    }
}
