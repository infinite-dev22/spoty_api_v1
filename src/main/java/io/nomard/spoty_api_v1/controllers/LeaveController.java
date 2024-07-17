package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.leave.LeaveServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("/leaves")
public class LeaveController {
    @Autowired
    private LeaveServiceImpl leaveService;

    @GetMapping("/all")
    public Flux<PageImpl<Leave>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "50") Integer pageSize) {
        return leaveService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Leave> getById(@RequestBody FindModel findModel) {
        return leaveService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Leave> getByContains(@Valid @RequestBody SearchModel searchModel) {
        return leaveService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Leave leave) {
        return leaveService.save(leave);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Leave leave) {
        return leaveService.update(leave);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return leaveService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return leaveService.deleteMultiple(idList);
    }
}
