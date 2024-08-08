package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Branch;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface BranchService {
    Page<Branch> getAll(int pageNo, int pageSize);

    Branch getById(Long id) throws NotFoundException;

    List<Branch> getByContains(String search);

    ResponseEntity<ObjectNode> save(Branch branch);

    ResponseEntity<ObjectNode> update(Branch branch) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
