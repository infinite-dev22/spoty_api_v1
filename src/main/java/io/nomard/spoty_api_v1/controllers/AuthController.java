package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.AuthenticationResponse;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.services.implementations.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody SignUpModel signUpDetails) throws NotFoundException {
        return authService.register(signUpDetails);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody LoginModel loginDetails) {
        return authService.login(loginDetails);
    }
}
