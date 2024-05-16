package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface BeneficiaryTypeService {
    List<BeneficiaryType> getAll(int pageNo, int pageSize);

    BeneficiaryType getById(Long id) throws NotFoundException;

    ArrayList<BeneficiaryType> getByContains(String search);

    ResponseEntity<ObjectNode> save(BeneficiaryType beneficiaryType);

    ResponseEntity<ObjectNode> update(BeneficiaryType beneficiaryType) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
