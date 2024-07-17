package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.BranchServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("branches")
public class BranchController {
    @Autowired
    private BranchServiceImpl branchService;

    @GetMapping("/all")
    public Flux<PageImpl<Branch>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "50") Integer pageSize) {
        return branchService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Branch> getById(@Valid @RequestBody FindModel findModel) {
        return branchService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Branch> getByContains(@Valid @RequestBody SearchModel searchModel) {
        return branchService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Branch branch) {
        return branchService.save(branch);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Branch branch) {
        return branchService.update(branch);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return branchService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return branchService.deleteMultiple(idList);
    }
}
