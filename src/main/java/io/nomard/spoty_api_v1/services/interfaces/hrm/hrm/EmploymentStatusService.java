package io.nomard.spoty_api_v1.services.interfaces.hrm.hrm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmploymentStatusService {
    Page<EmploymentStatus> getAll(int pageNo, int pageSize);

    EmploymentStatus getById(Long id) throws NotFoundException;

    List<EmploymentStatus> getByContains(String search);

    ResponseEntity<ObjectNode> save(EmploymentStatus employmentStatus);

    ResponseEntity<ObjectNode> update(EmploymentStatus employmentStatus) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
