package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {
    Page<Employee> getAll(int pageNo, int pageSize);

    Employee getById(Long id) throws NotFoundException;

    Employee getByEmail(String email) throws NotFoundException;

    List<Employee> getByContains(String search);

    ResponseEntity<ObjectNode> add(UserModel user) throws NotFoundException;

    ResponseEntity<ObjectNode> update(UserModel user) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
