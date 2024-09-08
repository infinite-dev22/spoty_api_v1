package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaleTransactionService {
    SaleTransaction getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(SaleDetail saleDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> update(SaleDetail saleDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
