package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.leave.LeaveServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/leaves")
public class LeaveController {
    @Autowired
    private LeaveServiceImpl leaveStatusService;


    @GetMapping("/all")
    public List<Leave> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return leaveStatusService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Leave getById(@RequestBody FindModel findModel) throws NotFoundException {
        return leaveStatusService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Leave leave) {
        return leaveStatusService.save(leave);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Leave leave) throws NotFoundException {
        return leaveStatusService.update(leave);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return leaveStatusService.delete(findModel.getId());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return leaveStatusService.deleteMultiple(idList);
    }
}