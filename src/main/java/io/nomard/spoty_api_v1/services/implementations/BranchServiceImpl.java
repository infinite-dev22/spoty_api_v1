package io.nomard.spoty_api_v1.services.implementations;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.BranchRepository;
import io.nomard.spoty_api_v1.services.interfaces.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {
    @Autowired
    private BranchRepository branchRepo;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    public List<Branch> getAll() {
        return branchRepo.findAll();
    }

    @Override
    public Branch getById(Long id) throws NotFoundException {
        Optional<Branch> branch = branchRepo.findById(id);
        if (branch.isEmpty()) {
            throw new NotFoundException();
        }
        return branch.get();
    }

    @Override
    public List<Branch> getByContains(String search) {
        return branchRepo.searchAll(search.toLowerCase());
    }

    @Override
    public Branch save(Branch branch) {
        branch.setCreatedBy(authService.authUser());
        branch.setCreatedAt(new Date());
        return branchRepo.saveAndFlush(branch);
    }

    @Override
    public Branch update(Long id, Branch branch) {
        branch.setId(id);
        branch.setUpdatedBy(authService.authUser());
        branch.setUpdatedAt(new Date());
        return branchRepo.saveAndFlush(branch);
    }

    @Override
    public String delete(Long id) {
        try {
            branchRepo.deleteById(id);
            return "Branch successfully deleted";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to delete Branch, Contact your system administrator for assistance";
        }
    }
}
