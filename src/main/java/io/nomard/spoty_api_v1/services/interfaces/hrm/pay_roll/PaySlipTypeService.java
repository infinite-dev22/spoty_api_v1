package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaySlipTypeService {
    Page<PaySlipType> getAll(int pageNo, int pageSize);

    PaySlipType getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(PaySlipType paySlipType);

    ResponseEntity<ObjectNode> update(PaySlipType paySlipType) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
