package io.nomard.spoty_api_v1.controllers;

import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import io.nomard.spoty_api_v1.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/users")
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping("/user")
    public User getById(@RequestBody Long id) throws NotFoundException {
        return userService.getById(id);
    }

    @PostMapping("/users/search")
    public List<User> getByContains(@RequestBody String search) {
        return userService.getByContains(search);
    }

    @PostMapping("/user/add")
    public ResponseEntity<String> save(@Valid @RequestBody SignUpModel signUpDetails) throws NotFoundException {
        return userService.save(signUpDetails);
    }

    @PutMapping("/user/update")
    public User update(@RequestBody Long id, @Valid @RequestBody User user) {
        return userService.update(id, user);
    }

    @PostMapping("/user/delete")
    public String delete(@RequestBody Long id) {
        return userService.delete(id);
    }
}
