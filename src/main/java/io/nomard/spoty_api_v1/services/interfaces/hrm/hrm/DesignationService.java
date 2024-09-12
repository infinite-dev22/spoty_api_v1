package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface DesignationService {
    Page<Designation> getAll(int pageNo, int pageSize);

    Designation getById(Long id) throws NotFoundException;

    ArrayList<Designation> getByContains(String search);

    ResponseEntity<ObjectNode> save(Designation designation);

    ResponseEntity<ObjectNode> update(Designation designation) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
