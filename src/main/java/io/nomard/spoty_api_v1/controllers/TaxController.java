package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.deductions.TaxServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("taxes")
public class TaxController {
    @Autowired
    private TaxServiceImpl taxService;

    @GetMapping("/all")
    public Flux<PageImpl<Tax>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "50") Integer pageSize) {
        return taxService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Tax> getById(@RequestBody FindModel findModel) {
        return taxService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Tax tax) {
        return taxService.save(tax);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Tax tax) {
        return taxService.update(tax);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return taxService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> ids) {
        return taxService.deleteMultiple(ids);
    }
}
