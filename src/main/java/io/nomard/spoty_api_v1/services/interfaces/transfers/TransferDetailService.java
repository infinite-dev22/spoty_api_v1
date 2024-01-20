package io.nomard.spoty_api_v1.services.interfaces.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface TransferDetailService {
    List<TransferDetail> getAll(int pageNo, int pageSize);

    TransferDetail getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(TransferDetail transferDetail);

    ResponseEntity<ObjectNode> saveMultiple(ArrayList<TransferDetail> transferDetailList);

    ResponseEntity<ObjectNode> update(TransferDetail transferDetail) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
