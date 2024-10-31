package io.nomard.spoty_api_v1.services.auth;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<ObjectNode> register(SignUpModel signUpDetails) throws NotFoundException;

    ResponseEntity<ObjectNode> login(LoginModel loginDetails) throws NotFoundException;

    Employee authUser();
}
