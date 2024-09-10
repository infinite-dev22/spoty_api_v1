package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.PaySlipServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payslip")
public class PaySlipController {
    @Autowired
    private PaySlipServiceImpl paySlipService;

    @GetMapping("/all")
    public Page<PaySlip> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return paySlipService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public PaySlip getById(@RequestBody FindModel findModel) throws NotFoundException {
        return paySlipService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public Page<PaySlip> getByContains(@RequestBody SearchModel searchModel) {
//        return paySlipService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody PaySlip paySlip) {
        return paySlipService.save(paySlip);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody PaySlip paySlip) throws NotFoundException {
        return paySlipService.update(paySlip);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return paySlipService.delete(findModel.getId());
    }
}
