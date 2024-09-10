package io.nomard.spoty_api_v1.services.interfaces.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuotationService {
    Page<QuotationMaster> getAll(int pageNo, int pageSize);

    QuotationMaster getById(Long id) throws NotFoundException;

    List<QuotationMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(QuotationMaster quotationMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> update(QuotationMaster quotationMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
