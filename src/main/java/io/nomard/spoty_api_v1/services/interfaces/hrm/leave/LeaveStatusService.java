package io.nomard.spoty_api_v1.services.interfaces.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveStatus;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LeaveStatusService {
    List<LeaveStatus> getAll(int pageNo, int pageSize);

    LeaveStatus getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(LeaveStatus bank);

    ResponseEntity<ObjectNode> update(LeaveStatus bank) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
