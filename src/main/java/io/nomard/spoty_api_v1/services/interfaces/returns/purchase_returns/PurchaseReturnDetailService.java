package io.nomard.spoty_api_v1.services.interfaces.returns.purchase_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface PurchaseReturnDetailService {
    List<PurchaseReturnDetail> getAll();

    PurchaseReturnDetail getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(PurchaseReturnDetail purchaseReturnDetail);

    ResponseEntity<ObjectNode> saveMultiple(ArrayList<PurchaseReturnDetail> purchaseReturnDetailList);

    ResponseEntity<ObjectNode> update(PurchaseReturnDetail purchaseReturnDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
