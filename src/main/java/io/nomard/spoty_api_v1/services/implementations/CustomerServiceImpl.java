package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.CustomerDTO;
import io.nomard.spoty_api_v1.utils.json_mapper.mappers.CustomerMapper;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CustomerRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private SpotyResponseImpl spotyResponseImpl;
    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Page<CustomerDTO> getAll(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        return customerRepo.findByTenantId(authService.authUser().getTenant().getId(), pageRequest).map(customer -> customerMapper.toDTO(customer));
    }

    @Override
    public CustomerDTO getById(Long id) throws NotFoundException {
        Optional<Customer> customer = customerRepo.findById(id);
        if (customer.isEmpty()) {
            throw new NotFoundException();
        }
        return customerMapper.toDTO(customer.get());
    }

    @Override
    public List<CustomerDTO> getByContains(String search) {
        return customerRepo.search(authService.authUser().getTenant().getId(), search.toLowerCase())
                .stream()
                .map(customer -> customerMapper.toDTO(customer))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> save(Customer customer) {
        customer.setTenant(authService.authUser().getTenant());
        if (Objects.isNull(customer.getBranch())) {
            customer.setBranch(authService.authUser().getBranch());
        }
        customer.setCreatedBy(authService.authUser());
        customer.setCreatedAt(LocalDateTime.now());
        try {
            customerRepo.save(customer);
            return spotyResponseImpl.created();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ObjectNode> update(Customer data) throws NotFoundException {
        var opt = customerRepo.findById(data.getId());

        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        var customer = opt.get();

        if (Objects.nonNull(data.getFirstName()) && !"".equalsIgnoreCase(data.getFirstName())) {
            customer.setFirstName(data.getFirstName());
        }

        if (Objects.nonNull(data.getOtherName()) && !"".equalsIgnoreCase(data.getOtherName())) {
            customer.setOtherName(data.getOtherName());
        }

        if (Objects.nonNull(data.getLastName()) && !"".equalsIgnoreCase(data.getLastName())) {
            customer.setLastName(data.getLastName());
        }

        if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
            customer.setEmail(data.getEmail());
        }

        if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
            customer.setPhone(data.getPhone());
        }

        if (Objects.nonNull(data.getCountry()) && !"".equalsIgnoreCase(data.getCountry())) {
            customer.setCountry(data.getCountry());
        }

        if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
            customer.setCity(data.getCity());
        }

        if (Objects.nonNull(data.getAddress()) && !"".equalsIgnoreCase(data.getAddress())) {
            customer.setAddress(data.getAddress());
        }

        if (Objects.nonNull(data.getTaxNumber()) && !"".equalsIgnoreCase(data.getTaxNumber())) {
            customer.setTaxNumber(data.getTaxNumber());
        }

        customer.setUpdatedBy(authService.authUser());
        customer.setUpdatedAt(LocalDateTime.now());

        try {
            customerRepo.save(customer);
            return spotyResponseImpl.ok();
        } catch (Exception e) {
            return spotyResponseImpl.error(e);
        }
    }

    @Override
    @Transactional
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
