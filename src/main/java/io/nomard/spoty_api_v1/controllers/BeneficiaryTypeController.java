package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.BeneficiaryTypeServiceImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("beneficiary/types")
public class BeneficiaryTypeController {
    @Autowired
    private BeneficiaryTypeServiceImpl beneficiaryTypeService;

    @GetMapping("/all")
    public Flux<PageImpl<BeneficiaryType>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                  @RequestParam(defaultValue = "50") Integer pageSize) {
        return beneficiaryTypeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<BeneficiaryType> getById(@RequestBody @NotNull FindModel findModel) {
        return beneficiaryTypeService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<BeneficiaryType> getByContains(@RequestBody @NotNull SearchModel searchModel) {
        return beneficiaryTypeService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody BeneficiaryType beneficiaryType) {
        return beneficiaryTypeService.save(beneficiaryType);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody BeneficiaryType beneficiaryType) {
        return beneficiaryTypeService.update(beneficiaryType);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return beneficiaryTypeService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return beneficiaryTypeService.deleteMultiple(idList);
    }
}
