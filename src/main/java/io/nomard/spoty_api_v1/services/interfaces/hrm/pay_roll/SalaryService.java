package io.nomard.spoty_api_v1.services.interfaces.hrm.pay_roll;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.Salary;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface SalaryService {
    Page<Salary> getAll(int pageNo, int pageSize);

    Salary getById(Long id) throws NotFoundException;

    ArrayList<Salary> getByContains(String search);

    ResponseEntity<ObjectNode> save(Salary salary);

    ResponseEntity<ObjectNode> update(Salary salary) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
