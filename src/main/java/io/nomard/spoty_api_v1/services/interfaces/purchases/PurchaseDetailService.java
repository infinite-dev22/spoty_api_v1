package io.nomard.spoty_api_v1.services.interfaces.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PurchaseDetailService {
    List<PurchaseDetail> getAll(int pageNo, int pageSize);

    PurchaseDetail getById(Long id);

    ResponseEntity<ObjectNode> save(PurchaseDetail purchaseDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<PurchaseDetail> purchaseDetailList);

    ResponseEntity<ObjectNode> update(PurchaseDetail purchaseDetail);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
