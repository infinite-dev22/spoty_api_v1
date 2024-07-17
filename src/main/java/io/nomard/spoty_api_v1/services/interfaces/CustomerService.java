package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Customer;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

public interface CustomerService {
    Flux<PageImpl<Customer>> getAll(int pageNo, int pageSize);

    Mono<Customer> getById(Long id);

    Flux<Customer> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> save(Customer customer);

    Mono<ResponseEntity<ObjectNode>> update(Customer customer);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);

    Mono<ResponseEntity<ObjectNode>> deleteMultiple(ArrayList<Long> idList);
}
