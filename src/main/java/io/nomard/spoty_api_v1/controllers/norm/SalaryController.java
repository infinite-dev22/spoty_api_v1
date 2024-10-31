package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.Salary;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.hrm.pay_roll.SalaryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("salaries")
public class SalaryController {
    @Autowired
    private SalaryServiceImpl salaryService;

    @GetMapping("/all")
    public Page<Salary> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                               @RequestParam(defaultValue = "50") Integer pageSize) {
        return salaryService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Salary getById(@RequestBody FindModel findModel) throws NotFoundException {
        return salaryService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<Salary> getByContains(@RequestBody SearchModel searchModel) {
        return salaryService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Salary salary) {
        return salaryService.save(salary);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Salary salary) throws NotFoundException {
        return salaryService.update(salary);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return salaryService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return salaryService.deleteMultiple(idList);
    }
}
