package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.leave.LeaveTypeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("leave/types")
public class LeaveTypeController {
    @Autowired
    private LeaveTypeServiceImpl leaveTypeService;

    @GetMapping("/all")
    public Flux<PageImpl<LeaveType>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "50") Integer pageSize) {
        return leaveTypeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<LeaveType> getById(@RequestBody FindModel findModel) {
        return leaveTypeService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody LeaveType leaveType) {
        return leaveTypeService.save(leaveType);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody LeaveType leaveType) {
        return leaveTypeService.update(leaveType);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return leaveTypeService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return leaveTypeService.deleteMultiple(idList);
    }
}
