package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;

import java.util.List;

public interface BranchService {
    List<Branch> getAll();

    Branch getById(Long id) throws NotFoundException;

    List<Branch> getByContains(String search);

    Branch save(Branch branch);

    Branch update(Long id, Branch branch);

    String delete(Long id);
}
