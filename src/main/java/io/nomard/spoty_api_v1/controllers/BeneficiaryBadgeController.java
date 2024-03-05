package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.BeneficiaryBadgeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("beneficiary/badges")
public class BeneficiaryBadgeController {
    @Autowired
    private BeneficiaryBadgeServiceImpl beneficiaryBadgeService;


    @GetMapping("/all")
    public List<BeneficiaryBadge> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        return beneficiaryBadgeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public BeneficiaryBadge getById(@RequestBody FindModel findModel) throws NotFoundException {
        return beneficiaryBadgeService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<BeneficiaryBadge> getByContains(@RequestBody SearchModel searchModel) {
//        return beneficiaryBadgeService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody BeneficiaryBadge beneficiaryBadge) {
        return beneficiaryBadgeService.save(beneficiaryBadge);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody BeneficiaryBadge beneficiaryBadge) throws NotFoundException {
        return beneficiaryBadgeService.update(beneficiaryBadge);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return beneficiaryBadgeService.delete(findModel.getId());
    }
}
