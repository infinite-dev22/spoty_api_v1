package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.LoginModel;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<ObjectNode> register(@Valid @RequestBody SignUpModel signUpDetails) throws NotFoundException {
        return authService.register(signUpDetails);
    }

    @PostMapping("/login")
    public ResponseEntity<ObjectNode> login(@Valid @RequestBody LoginModel loginDetails) throws NotFoundException {
        return authService.login(loginDetails);
    }

    @PutMapping("/reset")
    public ResponseEntity<ObjectNode> reset(@Valid @RequestBody LoginModel loginDetails) throws NotFoundException {
        return authService.login(loginDetails);
    }

    @PostMapping("/request/reset")
    public ResponseEntity<ObjectNode> requestReset(@Valid @RequestBody LoginModel loginDetails) throws NotFoundException {
        return authService.login(loginDetails);
    }
}
