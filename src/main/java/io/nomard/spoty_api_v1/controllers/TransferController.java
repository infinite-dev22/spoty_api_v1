package io.nomard.spoty_api_v1.controllers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.transfers.TransferDetailServiceImpl;
import io.nomard.spoty_api_v1.services.implementations.transfers.TransferMasterServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("transfers")
public class TransferController {
    @Autowired
    private TransferDetailServiceImpl transferDetailService;
    @Autowired
    private TransferMasterServiceImpl transferMasterService;

    // ADJUSTMENT MASTERS.
    @GetMapping("/masters")
    public List<TransferMaster> getAllMasters() {
        return transferMasterService.getAll();
    }

    @GetMapping("/master")
    public TransferMaster getMastersById(@RequestBody FindModel findModel) throws NotFoundException {
        return transferMasterService.getById(findModel.getId());
    }

//    @GetMapping("/masters/search")
//    public List<TransferMaster> getMastersByContains(@RequestBody SearchModel searchModel) {
//        return transferMasterService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/master/add")
    public ResponseEntity<ObjectNode> saveMaster(@Valid @RequestBody TransferMaster transferMaster) {
        return transferMasterService.save(transferMaster);
    }

    @PutMapping("/master/update")
    public ResponseEntity<ObjectNode> updateMaster(@Valid @RequestBody TransferMaster transferMaster) throws NotFoundException {
        return transferMasterService.update(transferMaster);
    }

    @DeleteMapping("/master/delete")
    public ResponseEntity<ObjectNode> deleteMaster(@RequestBody FindModel findModel) {
        return transferMasterService.delete(findModel.getId());
    }

    @DeleteMapping("/masters/delete")
    public ResponseEntity<ObjectNode> deleteMasters(@RequestBody ArrayList<Long> idList) {
        return transferMasterService.deleteMultiple(idList);
    }

    // ADJUSTMENT DETAILS.
    @GetMapping("/details")
    public List<TransferDetail> getAllDetails() {
        return transferDetailService.getAll();
    }

    @GetMapping("/detail")
    public TransferDetail getDetailById(@RequestBody FindModel findModel) throws NotFoundException {
        return transferDetailService.getById(findModel.getId());
    }

//    @GetMapping("/details/search")
//    public List<TransferDetail> getByContains(@RequestBody SearchModel searchModel) {
//        return transferDetailService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/detail/add")
    public ResponseEntity<ObjectNode> saveDetail(@Valid @RequestBody TransferDetail transferDetail) {
        return transferDetailService.save(transferDetail);
    }

    @PutMapping("/detail/update")
    public ResponseEntity<ObjectNode> updateDetail(@Valid @RequestBody TransferDetail transferDetail) throws NotFoundException {
        return transferDetailService.update(transferDetail);
    }

    @DeleteMapping("/detail/delete")
    public ResponseEntity<ObjectNode> deleteDetail(@RequestBody FindModel findModel) {
        return transferDetailService.delete(findModel.getId());
    }

    @DeleteMapping("/details/delete")
    public ResponseEntity<ObjectNode> deleteDetails(@RequestBody ArrayList<Long> idList) throws NotFoundException {
        return transferDetailService.deleteMultiple(idList);
    }
}
