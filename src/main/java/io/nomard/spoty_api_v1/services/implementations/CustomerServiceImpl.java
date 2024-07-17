package io.nomard.spoty_api_v1.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.repositories.CustomerRepository;
import io.nomard.spoty_api_v1.responses.SpotyResponseImpl;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<PageImpl<Customer>> getAll(int pageNo, int pageSize) {
        return authService.authUser()
                .flatMapMany(user -> customerRepo.findAllByTenantId(user.getTenant().getId(), PageRequest.of(pageNo, pageSize))
                        .collectList()
                        .zipWith(customerRepo.count())
                        .map(p -> new PageImpl<>(p.getT1(), PageRequest.of(pageNo, pageSize), p.getT2())));
    }

    @Override
    public Mono<Customer> getById(Long id) {
        return customerRepo.findById(id).switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Customer> getByContains(String search) {
        return authService.authUser()
                .flatMapMany(user -> customerRepo.search(
                        user.getTenant().getId(),
                        search.toLowerCase()
                ));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> save(Customer customer) {
        return authService.authUser()
                .flatMap(user -> {
                    customer.setTenant(user.getTenant());
                    if (Objects.isNull(customer.getBranch())) {
                        customer.setBranch(user.getBranch());
                    }
                    customer.setCreatedBy(user);
                    customer.setCreatedAt(new Date());
                    return customerRepo.save(customer)
                            .thenReturn(spotyResponseImpl.created());
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(new RuntimeException(e))));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> update(Customer data) {
        return customerRepo.findById(data.getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Supplier not found")))
                .flatMap(customer -> {
                    boolean updated = false;
                    if (Objects.nonNull(data.getName()) && !"".equalsIgnoreCase(data.getName())) {
                        customer.setName(data.getName());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getEmail()) && !"".equalsIgnoreCase(data.getEmail())) {
                        customer.setEmail(data.getEmail());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getCity()) && !"".equalsIgnoreCase(data.getCity())) {
                        customer.setCity(data.getCity());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getPhone()) && !"".equalsIgnoreCase(data.getPhone())) {
                        customer.setPhone(data.getPhone());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getAddress()) && !"".equalsIgnoreCase(data.getAddress())) {
                        customer.setAddress(data.getAddress());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getCountry()) && !"".equalsIgnoreCase(data.getCountry())) {
                        customer.setCountry(data.getCountry());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getCode()) && !"".equalsIgnoreCase(data.getCode())) {
                        customer.setCode(data.getCode());
                        updated = true;
                    }
                    if (Objects.nonNull(data.getTaxNumber()) && !"".equalsIgnoreCase(data.getTaxNumber())) {
                        customer.setTaxNumber(data.getTaxNumber());
                        updated = true;
                    }
                    if (updated) {
                        return authService.authUser()
                                .flatMap(user -> {
                                    customer.setUpdatedBy(user);
                                    customer.setUpdatedAt(new Date());
                                    return customerRepo.save(customer)
                                            .thenReturn(spotyResponseImpl.ok());
                                });
                    } else {
                        return Mono.just(spotyResponseImpl.ok());
                    }
                })
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ObjectNode>> delete(Long id) {
        return customerRepo.deleteById(id)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }

    @Override
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList) {
        return customerRepo.deleteAllById(idList)
                .thenReturn(spotyResponseImpl.ok())
                .onErrorResume(e -> Mono.just(spotyResponseImpl.error(e)));
    }
}
