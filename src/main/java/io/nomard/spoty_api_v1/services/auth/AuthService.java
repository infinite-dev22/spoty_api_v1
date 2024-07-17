package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<ResponseEntity<ObjectNode>> register(SignUpModel signUpDetails);

    Mono<ResponseEntity<ObjectNode>> login(LoginModel loginDetails);

    Mono<User> authUser();
}
