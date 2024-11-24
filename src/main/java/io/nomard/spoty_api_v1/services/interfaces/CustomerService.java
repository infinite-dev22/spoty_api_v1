package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.CustomerDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface CustomerService {
    Page<CustomerDTO> getAll(int pageNo, int pageSize);

    CustomerDTO getById(Long id) throws NotFoundException;

    List<CustomerDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(Customer customer);

    ResponseEntity<ObjectNode> update(Customer customer) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
