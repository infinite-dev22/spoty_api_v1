package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.EmployeeDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {
    Page<EmployeeDTO.EmployeeAsWholeDTO> getAll(int pageNo, int pageSize);

    EmployeeDTO.EmployeeAsWholeDTO getById(Long id) throws NotFoundException;

    Employee getByEmail(String email) throws NotFoundException;

    List<EmployeeDTO.EmployeeAsWholeDTO> getByContains(String search);

    ResponseEntity<ObjectNode> add(UserModel user) throws NotFoundException, MessagingException;

    ResponseEntity<ObjectNode> update(UserModel user) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
