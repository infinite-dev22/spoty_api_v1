package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CustomerRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    @Override
    public Customer getById(Long id) throws NotFoundException {
        Optional<Customer> customer = customerRepo.findById(id);
        if (customer.isEmpty()) {
            throw new NotFoundException();
        }
        return customer.get();
    }

    @Override
    public List<Customer> getByContains(String search) {
        return customerRepo.searchAll(search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Customer customer) {
        try {
            customer.setCreatedBy(authService.authUser());
            customer.setCreatedAt(new Date());
            customerRepo.saveAndFlush(customer);
            return spotyResponseImpl.created();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Long id, Customer customer) {
        try {
            customer.setUpdatedBy(authService.authUser());
            customer.setUpdatedAt(new Date());
            customer.setId(id);
            customerRepo.saveAndFlush(customer);
            return spotyResponseImpl.ok();
        } catch (Exception e){
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> delete(Long id) {
        try {
            customerRepo.deleteById(id);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
