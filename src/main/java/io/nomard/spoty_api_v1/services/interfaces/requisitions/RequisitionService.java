package io.nomard.spoty_api_v1.services.interfaces.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.RequisitionDTO;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RequisitionService {
    Page<RequisitionDTO> getAll(int pageNo, int pageSize);

    RequisitionDTO getById(Long id) throws NotFoundException;

    List<RequisitionDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(RequisitionMaster requisitionMaster);

    ResponseEntity<ObjectNode> update(RequisitionMaster requisitionMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
