package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.BeneficiaryBadgeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("beneficiary/badges")
public class BeneficiaryBadgeController {
    @Autowired
    private BeneficiaryBadgeServiceImpl beneficiaryBadgeService;

    @GetMapping("/all")
    public Flux<PageImpl<BeneficiaryBadge>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return beneficiaryBadgeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<BeneficiaryBadge> getById(@RequestBody FindModel findModel) {
        return beneficiaryBadgeService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<BeneficiaryBadge> getByContains(@RequestBody SearchModel searchModel) {
        return beneficiaryBadgeService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody BeneficiaryBadge beneficiaryBadge) {
        return beneficiaryBadgeService.save(beneficiaryBadge);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody BeneficiaryBadge beneficiaryBadge) {
        return beneficiaryBadgeService.update(beneficiaryBadge);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return beneficiaryBadgeService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return beneficiaryBadgeService.deleteMultiple(idList);
    }
}
