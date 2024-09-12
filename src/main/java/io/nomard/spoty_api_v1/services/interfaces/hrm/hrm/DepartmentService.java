package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface DepartmentService {
    Page<Department> getAll(int pageNo, int pageSize);

    Department getById(Long id) throws NotFoundException;

    ArrayList<Department> getByContains(String search);

    ResponseEntity<ObjectNode> save(Department department);

    ResponseEntity<ObjectNode> update(Department department) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> delete(List<Long> idList) throws NotFoundException;
}
