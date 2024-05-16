package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CustomerRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;

    @Override
    public List<Customer> getAll(int pageNo, int pageSize) {
        //create page request object
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize/*, Sort.by("createdAt").descending()*/);
        //pass it to repos
        Page<Customer> page = customerRepo.findAll(pageRequest);
        //page.hasContent(); -- to check pages are there or not
        return page.getContent();
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
        return customerRepo.searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCaseOrCountryContainingIgnoreCase(
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase(),
                search.toLowerCase());
    }

    @Override
    public ResponseEntity<ObjectNode> save(Customer customer) {
        try {
            customer.setCreatedBy(authService.authUser());
            customer.setCreatedAt(new Date());
            customerRepo.saveAndFlush(customer);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    public ResponseEntity<ObjectNode> update(Customer data) throws NotFoundException {
        var opt = customerRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var customer = opt.get();

        if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
            customer.setName(data.getName());
        }

        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            customer.setEmail(data.getEmail());
        }

        if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
            customer.setCity(data.getCity());
        }

        if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
            customer.setPhone(data.getPhone());
        }

        if (Objects.nonNull(data.getAddress()) && !"".equalsIgnoreCase(data.getAddress())) {
            customer.setAddress(data.getAddress());
        }

        if (Objects.nonNull(data.getCountry()) && !"".equalsIgnoreCase(data.getCountry())) {
            customer.setCountry(data.getCountry());
        }

        if (Objects.nonNull(data.getCode()) && !"".equalsIgnoreCase(data.getCode())) {
            customer.setCode(data.getCode());
        }

        if (Objects.nonNull(data.getTaxNumber()) && !"".equalsIgnoreCase(data.getTaxNumber())) {
            customer.setTaxNumber(data.getTaxNumber());
        }

        customer.setUpdatedBy(authService.authUser());
        customer.setUpdatedAt(new Date());

        try {
            customerRepo.saveAndFlush(customer);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
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

    @Override
    public ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList) {
        try {
            customerRepo.deleteAllById(idList);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }
}
