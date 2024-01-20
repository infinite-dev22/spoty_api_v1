package io.nomard.spoty_api_v1.services.interfaces.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface TransferMasterService {
    List<TransferMaster> getAll(int pageNo, int pageSize);

    TransferMaster getById(Long id) throws NotFoundException;

    List<TransferMaster> getByContains(String search);

    ResponseEntity<ObjectNode> save(TransferMaster transferMaster);

    ResponseEntity<ObjectNode> update(TransferMaster transferMaster) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
