package io.nomard.spoty_api_v1.services.interfaces.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface QuotationMasterService {
    List<QuotationMaster> getAll();

    QuotationMaster getById(Long id) throws NotFoundException;

    List<QuotationMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(QuotationMaster quotationMaster);

    ResponseEntity<ObjectNode> update(QuotationMaster quotationMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
