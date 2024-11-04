package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.EmploymentStatusDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmploymentStatusService {
    Page<EmploymentStatusDTO.EmploymentStatusAsWholeDTO> getAll(int pageNo, int pageSize);

    EmploymentStatusDTO.EmploymentStatusAsWholeDTO getById(Long id) throws NotFoundException;

    List<EmploymentStatusDTO.EmploymentStatusAsWholeDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(EmploymentStatus employmentStatus);

    ResponseEntity<ObjectNode> update(EmploymentStatus employmentStatus) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
