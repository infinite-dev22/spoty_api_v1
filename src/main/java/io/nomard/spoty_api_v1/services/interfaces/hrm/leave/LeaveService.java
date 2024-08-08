package io.nomard.spoty_api_v1.services.interfaces.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.Leave;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LeaveService {
    Page<Leave> getAll(int pageNo, int pageSize);

    Leave getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Leave leave);

    ResponseEntity<ObjectNode> update(Leave leave) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
