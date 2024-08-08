package io.nomard.spoty_api_v1.services.interfaces.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LeaveTypeService {
    Page<LeaveType> getAll(int pageNo, int pageSize);

    LeaveType getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(LeaveType leaveType);

    ResponseEntity<ObjectNode> update(LeaveType leaveType) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
