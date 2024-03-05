package io.nomard.spoty_api_v1.services.interfaces.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LeaveTypeService {
    List<LeaveType> getAll(int pageNo, int pageSize);

    LeaveType getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(LeaveType bank);

    ResponseEntity<ObjectNode> update(LeaveType bank) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
