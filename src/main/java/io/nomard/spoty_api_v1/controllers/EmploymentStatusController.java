package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.hrm.EmploymentStatusServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("employment/statuses")
public class EmploymentStatusController {
    @Autowired
    private EmploymentStatusServiceImpl designationService;


    @GetMapping("/all")
    public List<EmploymentStatus> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        return designationService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public EmploymentStatus getById(@RequestBody FindModel findModel) throws NotFoundException {
        return designationService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<EmploymentStatus> getByContains(@RequestBody SearchModel searchModel) {
//        return designationService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody EmploymentStatus designation) {
        return designationService.save(designation);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody EmploymentStatus designation) throws NotFoundException {
        return designationService.update(designation);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return designationService.delete(findModel.getId());
    }
}
