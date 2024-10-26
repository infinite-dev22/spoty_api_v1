package io.nomard.spoty_api_v1.services.interfaces.adjustments;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.AdjustmentDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdjustmentService {
    Page<AdjustmentDTO> getAll(int pageNo, int pageSize);

    AdjustmentDTO getById(Long id) throws NotFoundException;

    List<AdjustmentDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(AdjustmentMaster adjustmentMaster);

    ResponseEntity<ObjectNode> update(AdjustmentMaster adjustmentMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
