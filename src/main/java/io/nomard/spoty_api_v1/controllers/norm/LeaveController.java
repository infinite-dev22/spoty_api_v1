package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.leave.LeaveServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/leaves")
public class LeaveController {
    @Autowired
    private LeaveServiceImpl leaveStatusService;

    @GetMapping("/all")
    public Page<Leave> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
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

    @PutMapping("/approve")
    public ResponseEntity<ObjectNode> approve(@Valid @RequestBody ApprovalModel approvalModel) throws NotFoundException {
        return leaveStatusService.approve(approvalModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return leaveStatusService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return leaveStatusService.deleteMultiple(idList);
    }
}
