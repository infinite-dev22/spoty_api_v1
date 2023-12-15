package io.nomard.spoty_api_v1.services.interfaces.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PurchaseDetailService {
    List<PurchaseDetail> getAll();

    PurchaseDetail getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(PurchaseDetail purchaseDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<PurchaseDetail> purchaseDetailList);

    ResponseEntity<ObjectNode> update(PurchaseDetail purchaseDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
