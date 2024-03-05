package io.nomard.spoty_api_v1.services.interfaces.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RequisitionMasterService {
    List<RequisitionMaster> getAll(int pageNo, int pageSize);

    RequisitionMaster getById(Long id) throws NotFoundException;

    List<RequisitionMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(RequisitionMaster requisitionMaster);

    ResponseEntity<ObjectNode> update(RequisitionMaster requisitionMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
