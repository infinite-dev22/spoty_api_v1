package io.nomard.spoty_api_v1.services.interfaces.hrm.leave;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LeaveTypeService {
    Flux<PageImpl<LeaveType>> getAll(int pageNo, int pageSize);

    Mono<LeaveType> getById(Long id);

    Mono<ResponseEntity<ObjectNode>> save(LeaveType leaveType);

    Mono<ResponseEntity<ObjectNode>> update(LeaveType leaveType);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(List<Long> idList);
}
