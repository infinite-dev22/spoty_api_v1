package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.AuthenticationResponse;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> register(SignUpModel signUpDetails) throws NotFoundException;

    AuthenticationResponse login(LoginModel loginDetails) throws NotFoundException;
}
