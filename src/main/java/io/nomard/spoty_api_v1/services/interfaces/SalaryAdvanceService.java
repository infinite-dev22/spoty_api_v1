package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.SalaryAdvance;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SalaryAdvanceService {
    List<SalaryAdvance> getAll(int pageNo, int pageSize);

    SalaryAdvance getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(SalaryAdvance salaryAdvance);

    ResponseEntity<ObjectNode> update(SalaryAdvance salaryAdvance) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
