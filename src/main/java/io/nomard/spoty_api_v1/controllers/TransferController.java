package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.transfers.TransferMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transfers")
public class TransferController {
    @Autowired
    private TransferMasterServiceImpl transferMasterService;

    @GetMapping("/all")
    public Page<TransferMaster> getAllMasters(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return transferMasterService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public TransferMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return transferMasterService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<TransferMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
        return transferMasterService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody TransferMaster transferMaster) {
        return transferMasterService.save(transferMaster);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody TransferMaster transferMaster) throws NotFoundException {
        return transferMasterService.update(transferMaster);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return transferMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody List<Long> idList) {
        return transferMasterService.deleteMultiple(idList);
    }
}
