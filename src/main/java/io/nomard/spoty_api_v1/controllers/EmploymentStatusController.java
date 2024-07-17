package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.hrm.EmploymentStatusServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("employment/statuses")
public class EmploymentStatusController {
    @Autowired
    private EmploymentStatusServiceImpl employmentStatusService;

    @GetMapping("/all")
    public Flux<PageImpl<EmploymentStatus>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return employmentStatusService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<EmploymentStatus> getById(@RequestBody FindModel findModel) {
        return employmentStatusService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<EmploymentStatus> getByContains(@RequestBody SearchModel searchModel) {
        return employmentStatusService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody EmploymentStatus employmentStatus) {
        return employmentStatusService.save(employmentStatus);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody EmploymentStatus employmentStatus) {
        return employmentStatusService.update(employmentStatus);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return employmentStatusService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return employmentStatusService.deleteMultiple(idList);
    }
}
