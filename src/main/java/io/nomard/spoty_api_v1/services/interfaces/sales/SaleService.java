package io.nomard.spoty_api_v1.services.interfaces.sales;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.SaleDTO;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaleService {
    Page<SaleDTO> getAll(int pageNo, int pageSize);

    SaleDTO getById(Long id) throws NotFoundException;

    List<SaleDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(SaleMaster saleMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> update(SaleMaster saleMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
