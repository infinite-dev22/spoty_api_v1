package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.DesignationDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface DesignationService {
    Page<DesignationDTO.DesignationAsWholeDTO> getAll(int pageNo, int pageSize);

    DesignationDTO.DesignationAsWholeDTO getById(Long id) throws NotFoundException;

    List<DesignationDTO.DesignationAsWholeDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(Designation designation);

    ResponseEntity<ObjectNode> update(Designation designation) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
