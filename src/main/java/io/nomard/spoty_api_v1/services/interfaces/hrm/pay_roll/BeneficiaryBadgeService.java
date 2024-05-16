package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface BeneficiaryBadgeService {
    List<BeneficiaryBadge> getAll(int pageNo, int pageSize);

    BeneficiaryBadge getById(Long id) throws NotFoundException;

    List<BeneficiaryBadge> getByContains(String search);

    ResponseEntity<ObjectNode> save(BeneficiaryBadge beneficiaryBadge);

    ResponseEntity<ObjectNode> update(BeneficiaryBadge beneficiaryBadge) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) throws NotFoundException;
}
