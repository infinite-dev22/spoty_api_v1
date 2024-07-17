package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    Flux<PageImpl<User>> getAll(int pageNo, int pageSize);

    Mono<User> getById(Long id);

    Mono<User> getByEmail(String email);

    Flux<List<User>> getByContains(String search);

    Mono<ResponseEntity<ObjectNode>> add(UserModel user);

    Mono<ResponseEntity<ObjectNode>> update(UserModel user);

    Mono<ResponseEntity<ObjectNode>> delete(Long id);
}
