package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.PaySlipTypeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("payslip/types")
public class PaySlipTypeController {
    @Autowired
    private PaySlipTypeServiceImpl paySlipTypeService;

    @GetMapping("/all")
    public Flux<PageImpl<PaySlipType>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return paySlipTypeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<PaySlipType> getById(@RequestBody FindModel findModel) {
        return paySlipTypeService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody PaySlipType paySlipType) {
        return paySlipTypeService.save(paySlipType);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody PaySlipType paySlipType) {
        return paySlipTypeService.update(paySlipType);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return paySlipTypeService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody ArrayList<Long> ids) {
        return paySlipTypeService.deleteMultiple(ids);
    }
}
