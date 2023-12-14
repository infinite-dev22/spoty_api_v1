package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {
    List<Customer> getAll();

    Customer getById(Long id) throws NotFoundException;

    List<Customer> getByContains(String search);

    ResponseEntity<ObjectNode> save(Customer customer) throws NotFoundException;

    ResponseEntity<ObjectNode> update(Long id, Customer customer);

    ResponseEntity<ObjectNode> delete(Long id);
}
