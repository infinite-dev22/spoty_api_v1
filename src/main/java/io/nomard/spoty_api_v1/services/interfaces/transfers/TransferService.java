package io.nomard.spoty_api_v1.services.interfaces.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.TransferDTO;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.ApprovalModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransferService {
    Page<TransferDTO> getAll(int pageNo, int pageSize);

    TransferDTO getById(Long id) throws NotFoundException;

    List<TransferDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(TransferMaster transferMaster);

    ResponseEntity<ObjectNode> update(TransferMaster transferMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> approve(ApprovalModel approvalModel) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
