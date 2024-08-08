package io.nomard.spoty_api_v1.services.interfaces.purchases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PurchaseMasterService {
    Page<PurchaseMaster> getAll(int pageNo, int pageSize);

    PurchaseMaster getById(Long id) throws NotFoundException;

    List<PurchaseMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(PurchaseMaster purchaseMaster);

    ResponseEntity<ObjectNode> update(PurchaseMaster purchaseMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
