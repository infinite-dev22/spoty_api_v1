package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.leave.LeaveStatusServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("leave_status")
public class LeaveStatusController {
    @Autowired
    private LeaveStatusServiceImpl leaveStatusService;


    @GetMapping("/all")
    public List<LeaveStatus> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                    @RequestParam(defaultValue = "20") Integer pageSize) {
        return leaveStatusService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public LeaveStatus getById(@RequestBody FindModel findModel) throws NotFoundException {
        return leaveStatusService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<LeaveStatus> getByContains(@RequestBody SearchModel searchModel) {
//        return leaveStatusService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody LeaveStatus leaveStatus) {
        return leaveStatusService.save(leaveStatus);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody LeaveStatus leaveStatus) throws NotFoundException {
        return leaveStatusService.update(leaveStatus);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return leaveStatusService.delete(findModel.getId());
    }
}
