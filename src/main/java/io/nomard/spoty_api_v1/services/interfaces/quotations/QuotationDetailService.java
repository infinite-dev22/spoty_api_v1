package io.nomard.spoty_api_v1.services.interfaces.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.quotations.QuotationDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuotationDetailService {
    List<QuotationDetail> getAll(int pageNo, int pageSize);

    QuotationDetail getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(QuotationDetail quotationDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<QuotationDetail> quotationDetailList);

    ResponseEntity<ObjectNode> update(QuotationDetail quotationDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
