package io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PurchaseReturnDetailService {
    List<PurchaseReturnDetail> getAll(int pageNo, int pageSize);

    PurchaseReturnDetail getById(Long id);

    ResponseEntity<ObjectNode> save(PurchaseReturnDetail purchaseReturnDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<PurchaseReturnDetail> purchaseReturnDetailList);

    ResponseEntity<ObjectNode> update(PurchaseReturnDetail purchaseReturnDetail);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
