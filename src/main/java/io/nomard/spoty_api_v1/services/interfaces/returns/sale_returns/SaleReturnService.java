package io.nomard.spoty_api_v1.services.interfaces.returns.sale_returns;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.SaleReturnDTO;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SaleReturnService {
    Page<SaleReturnDTO> getAll(int pageNo, int pageSize);

    SaleReturnDTO getById(Long id) throws NotFoundException;

    List<SaleReturnDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(SaleReturnMaster saleMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> update(SaleReturnMaster saleMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
