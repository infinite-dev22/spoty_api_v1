package io.nomard.spoty_api_v1.services.interfaces.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.PurchaseDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PurchaseService {
    Page<PurchaseDTO> getAll(int pageNo, int pageSize);

    PurchaseDTO getById(Long id) throws NotFoundException;

    List<PurchaseDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(PurchaseMaster purchaseMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> update(PurchaseMaster purchaseMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> cancel(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
