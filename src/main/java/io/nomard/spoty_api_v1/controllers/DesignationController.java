package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.hrm.DesignationServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("designations")
public class DesignationController {
    @Autowired
    private DesignationServiceImpl designationService;

    @GetMapping("/all")
    public Flux<PageImpl<Designation>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return designationService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Designation> getById(@RequestBody FindModel findModel) {
        return designationService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Designation> getByContains(@RequestBody SearchModel searchModel) {
        return designationService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Designation designation) {
        return designationService.save(designation);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Designation designation) {
        return designationService.update(designation);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return designationService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return designationService.deleteMultiple(idList);
    }
}
