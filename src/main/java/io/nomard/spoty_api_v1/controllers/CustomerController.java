package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.CustomerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerServiceImpl customerService;


    @GetMapping("/all")
    public List<Customer> getAll() {
        return customerService.getAll();
    }

    @PostMapping("/single")
    public Customer getById(@RequestBody Long id) throws NotFoundException {
        return customerService.getById(id);
    }

    @PostMapping("/search")
    public List<Customer> getByContains(@RequestBody String search) {
        return customerService.getByContains(search);
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Customer customer) {
        customer.setCreatedAt(new Date());
        return customerService.save(customer);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody Customer customer) {
        customer.setId(id);
        customer.setUpdatedAt(new Date());
        return customerService.update(id, customer);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return customerService.delete(id);
    }
}
