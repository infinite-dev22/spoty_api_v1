package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Salary;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SalaryService {
    List<Salary> getAll(int pageNo, int pageSize);

    Salary getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Salary salary);

    ResponseEntity<ObjectNode> update(Salary salary) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
