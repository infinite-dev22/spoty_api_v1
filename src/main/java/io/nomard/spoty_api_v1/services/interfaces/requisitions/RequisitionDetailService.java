package io.nomard.spoty_api_v1.services.interfaces.requisitions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionDetail;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RequisitionDetailService {
    List<RequisitionDetail> getAll(int pageNo, int pageSize);

    RequisitionDetail getById(Long id);

    ResponseEntity<ObjectNode> save(RequisitionDetail requisitionDetail);

    ResponseEntity<ObjectNode> saveMultiple(List<RequisitionDetail> requisitionDetailList);

    ResponseEntity<ObjectNode> update(RequisitionDetail requisitionDetail);

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList);
}
