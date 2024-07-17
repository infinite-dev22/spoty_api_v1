package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<ObjectNode>> register(@Valid @RequestBody SignUpModel signUpDetails) {
        return authService.register(signUpDetails);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ObjectNode>> login(@Valid @RequestBody LoginModel loginDetails) {
        return authService.login(loginDetails);
    }

    @PutMapping("/reset")
    public Mono<ResponseEntity<ObjectNode>> reset(@Valid @RequestBody LoginModel loginDetails) {
        return authService.login(loginDetails);
    }

    @PostMapping("/request/reset")
    public Mono<ResponseEntity<ObjectNode>> requestReset(@Valid @RequestBody LoginModel loginDetails) {
        return authService.login(loginDetails);
    }
}
