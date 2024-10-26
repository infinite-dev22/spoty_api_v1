package io.nomard.spoty_api_v1.controllers.norm.accounting;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.ExpenseDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.accounting.ExpenseServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseServiceImpl expenseService;

    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<ExpenseDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return expenseService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public ExpenseDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return expenseService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<ExpenseDTO> getByContains(@RequestBody SearchModel searchModel) {
        return expenseService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Expense expense) {
        return expenseService.save(expense);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Expense expense) throws NotFoundException {
        return expenseService.update(expense);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return expenseService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return expenseService.deleteMultiple(idList);
    }
}
