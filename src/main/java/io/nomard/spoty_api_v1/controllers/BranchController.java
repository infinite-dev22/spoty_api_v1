package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.BranchServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class BranchController {
    @Autowired
    private BranchServiceImpl branchService;


    @GetMapping("/branches")
    public List<Branch> getAll() {
        return branchService.getAll();
    }

    @PostMapping("/branch")
    public Branch getById(@Valid @RequestBody FindModel findModel) throws NotFoundException {
        return branchService.getById(findModel.getId());
    }

    @PostMapping("/branches/search")
    public List<Branch> getByContains(@Valid @RequestBody SearchModel searchModel) {
        return branchService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/branch/add")
    public Branch save(@Valid @RequestBody Branch branch) {
        branch.setCreatedAt(new Date());
        return branchService.save(branch);
    }

    @PutMapping("/branch/update")
    public Branch update(@RequestBody Long id, @Valid @RequestBody Branch branch) {
        branch.setId(id);
        branch.setUpdatedAt(new Date());
        return branchService.update(id, branch);
    }

    @PostMapping("/branch/delete")
    public String delete(@RequestBody Long id) {
        return branchService.delete(id);
    }
}
