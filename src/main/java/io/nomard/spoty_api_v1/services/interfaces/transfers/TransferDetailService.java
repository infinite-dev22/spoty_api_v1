package io.nomard.spoty_api_v1.services.interfaces.transfers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransferDetailService {
    List<TransferDetail> getAll(int pageNo, int pageSize);

    TransferDetail getById(Long id);

    ResponseEntity<ObjectNode> save(TransferDetail transferDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<TransferDetail> transferDetailList);

    ResponseEntity<ObjectNode> update(TransferDetail transferDetail);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
