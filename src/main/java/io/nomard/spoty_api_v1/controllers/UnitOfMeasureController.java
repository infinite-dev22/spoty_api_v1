package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.UnitOfMeasureServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("/units_of_measure")
public class UnitOfMeasureController {
    @Autowired
    private UnitOfMeasureServiceImpl unit_of_measureService;

    @GetMapping("/all")
    public Flux<PageImpl<UnitOfMeasure>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "50") Integer pageSize) {
        return unit_of_measureService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<UnitOfMeasure> getById(@RequestBody FindModel findModel) {
        return unit_of_measureService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<UnitOfMeasure> getByContains(@RequestBody SearchModel searchModel) {
        return unit_of_measureService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody UnitOfMeasure unit_of_measure) {
        return unit_of_measureService.save(unit_of_measure);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody UnitOfMeasure unit_of_measure) {
        return unit_of_measureService.update(unit_of_measure);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return unit_of_measureService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return unit_of_measureService.deleteMultiple(idList);
    }
}
