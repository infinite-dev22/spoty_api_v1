package io.nomard.spoty_api_v1.services.interfaces.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdjustmentMasterService {
    Page<AdjustmentMaster> getAll(int pageNo, int pageSize);

    AdjustmentMaster getById(Long id) throws NotFoundException;

    List<AdjustmentMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(AdjustmentMaster adjustmentMaster);

    ResponseEntity<ObjectNode> update(AdjustmentMaster adjustmentMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
