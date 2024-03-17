package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.SalaryAdvance;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.SalaryAdvanceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("salary/advances")
public class SalaryAdvanceController {
    @Autowired
    private SalaryAdvanceServiceImpl salaryAdvanceService;


    @GetMapping("/all")
    public List<SalaryAdvance> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "50") Integer pageSize) {
        return salaryAdvanceService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public SalaryAdvance getById(@RequestBody FindModel findModel) throws NotFoundException {
        return salaryAdvanceService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<SalaryAdvance> getByContains(@RequestBody SearchModel searchModel) {
//        return salaryAdvanceService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody SalaryAdvance salaryAdvance) {
        return salaryAdvanceService.save(salaryAdvance);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody SalaryAdvance salaryAdvance) throws NotFoundException {
        return salaryAdvanceService.update(salaryAdvance);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return salaryAdvanceService.delete(findModel.getId());
    }
}
