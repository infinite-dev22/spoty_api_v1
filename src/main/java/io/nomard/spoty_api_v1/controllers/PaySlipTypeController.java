package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.PaySlipTypeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("payslip/types")
public class PaySlipTypeController {
    @Autowired
    private PaySlipTypeServiceImpl paySlipTypeService;

    @GetMapping("/all")
    public Page<PaySlipType> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return paySlipTypeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PaySlipType getById(@RequestBody FindModel findModel) throws NotFoundException {
        return paySlipTypeService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<PaySlipType> getByContains(@RequestBody SearchModel searchModel) {
//        return paySlipTypeService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody PaySlipType paySlipType) {
        return paySlipTypeService.save(paySlipType);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody PaySlipType paySlipType) throws NotFoundException {
        return paySlipTypeService.update(paySlipType);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return paySlipTypeService.delete(findModel.getId());
    }
}
