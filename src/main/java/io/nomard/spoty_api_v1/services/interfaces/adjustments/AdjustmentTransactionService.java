package io.nomard.spoty_api_v1.services.interfaces.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdjustmentTransactionService {
    AdjustmentTransaction getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(AdjustmentDetail adjustmentDetail);

    ResponseEntity<ObjectNode> update(AdjustmentDetail adjustmentDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
