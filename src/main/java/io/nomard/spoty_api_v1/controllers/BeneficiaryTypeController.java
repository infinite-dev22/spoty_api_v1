package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.BeneficiaryTypeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("beneficiary/types")
public class BeneficiaryTypeController {
    @Autowired
    private BeneficiaryTypeServiceImpl beneficiaryTypeService;


    @GetMapping("/all")
    public List<BeneficiaryType> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        return beneficiaryTypeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public BeneficiaryType getById(@RequestBody FindModel findModel) throws NotFoundException {
        return beneficiaryTypeService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<BeneficiaryType> getByContains(@RequestBody SearchModel searchModel) {
//        return beneficiaryTypeService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody BeneficiaryType beneficiaryType) {
        return beneficiaryTypeService.save(beneficiaryType);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody BeneficiaryType beneficiaryType) throws NotFoundException {
        return beneficiaryTypeService.update(beneficiaryType);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return beneficiaryTypeService.delete(findModel.getId());
    }
}
