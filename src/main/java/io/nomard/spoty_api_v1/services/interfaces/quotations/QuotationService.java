package io.nomard.spoty_api_v1.services.interfaces.quotations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.json_mapper.dto.QuotationDTO;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuotationService {
    Page<QuotationDTO> getAll(int pageNo, int pageSize);

    QuotationDTO getById(Long id) throws NotFoundException;

    List<QuotationDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(QuotationMaster quotationMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> update(QuotationMaster quotationMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
