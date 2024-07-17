package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.PaySlipServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("payslip")
public class PaySlipController {
    @Autowired
    private PaySlipServiceImpl paySlipService;

    @GetMapping("/all")
    public Flux<PageImpl<PaySlip>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        return paySlipService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<PaySlip> getById(@RequestBody FindModel findModel) {
        return paySlipService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody PaySlip paySlip) {
        return paySlipService.save(paySlip);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody PaySlip paySlip) {
        return paySlipService.update(paySlip);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return paySlipService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return paySlipService.deleteMultiple(idList);
    }
}
