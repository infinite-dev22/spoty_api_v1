package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.BranchServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("branches")
public class BranchController {
    @Autowired
    private BranchServiceImpl branchService;


    @GetMapping("/all")
    public List<Branch> getAll() {
        return branchService.getAll();
    }

    @PostMapping("/single")
    public Branch getById(@Valid @RequestBody FindModel findModel) throws NotFoundException {
        return branchService.getById(findModel.getId());
    }

    @PostMapping("/search")
    public List<Branch> getByContains(@Valid @RequestBody SearchModel searchModel) {
        return branchService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Branch branch) {
        branch.setCreatedAt(new Date());
        return branchService.save(branch);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Branch branch) throws NotFoundException {
        branch.setUpdatedAt(new Date());
        return branchService.update(branch);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return branchService.delete(findModel.getId());
    }
}
