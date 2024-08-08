package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SaleTermAndCondition;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaleTermAndConditionService {
    Page<SaleTermAndCondition> getAll(int pageNo, int pageSize);

    SaleTermAndCondition getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(SaleTermAndCondition saleTermAndCondition);

    ResponseEntity<ObjectNode> update(SaleTermAndCondition saleTermAndCondition) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
