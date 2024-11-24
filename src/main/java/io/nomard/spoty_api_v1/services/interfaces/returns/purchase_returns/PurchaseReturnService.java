package io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PurchaseReturnDTO;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PurchaseReturnService {
    Page<PurchaseReturnDTO> getAll(int pageNo, int pageSize);

    PurchaseReturnDTO getById(Long id) throws NotFoundException;

    List<PurchaseReturnDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(PurchaseReturnMaster purchaseReturnMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> update(PurchaseReturnMaster purchaseReturnMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
