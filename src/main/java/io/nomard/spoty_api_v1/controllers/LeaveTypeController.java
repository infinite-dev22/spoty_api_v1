package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.leave.LeaveTypeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("leave/types")
public class LeaveTypeController {
    @Autowired
    private LeaveTypeServiceImpl leaveTypeService;

    @GetMapping("/all")
    public List<LeaveType> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                  @RequestParam(defaultValue = "50") Integer pageSize) {
        return leaveTypeService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public LeaveType getById(@RequestBody FindModel findModel) throws NotFoundException {
        return leaveTypeService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<LeaveType> getByContains(@RequestBody SearchModel searchModel) {
//        return leaveTypeService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody LeaveType leaveType) {
        return leaveTypeService.save(leaveType);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody LeaveType leaveType) throws NotFoundException {
        return leaveTypeService.update(leaveType);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return leaveTypeService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return leaveTypeService.deleteMultiple(idList);
    }
}
