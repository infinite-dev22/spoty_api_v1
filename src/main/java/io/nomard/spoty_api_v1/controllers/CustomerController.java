package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.CustomerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerServiceImpl customerService;

    @GetMapping("/all")
    public Flux<PageImpl<Customer>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "50") Integer pageSize) {
        return customerService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Customer> getById(@RequestBody FindModel findModel) {
        return customerService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Customer> getByContains(@RequestBody SearchModel searchModel) {
        return customerService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Customer customer) {
        customer.setCreatedAt(new Date());
        return customerService.save(customer);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Customer customer) {
        return customerService.update(customer);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return customerService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> deleteMultiple(@RequestBody ArrayList<Long> idList) {
        return customerService.deleteMultiple(idList);
    }
}
