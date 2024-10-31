package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DepartmentDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DepartmentService {
    Page<DepartmentDTO.DepartmentAsWholeDTO> getAll(int pageNo, int pageSize);

    DepartmentDTO.DepartmentAsWholeDTO getById(Long id) throws NotFoundException;

    List<DepartmentDTO.DepartmentAsWholeDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(Department department);

    ResponseEntity<ObjectNode> update(Department department) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> delete(List<Long> idList) throws NotFoundException;
}
