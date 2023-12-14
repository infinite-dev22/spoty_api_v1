package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/all")
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping("/single")
    public User getById(@RequestBody Long id) throws NotFoundException {
        return userService.getById(id);
    }

    @PostMapping("/search")
    public List<User> getByContains(@RequestBody String search) {
        return userService.getByContains(search);
    }

    @PostMapping("/add")
    public ObjectNode save(@Valid @RequestBody User user) throws NotFoundException {
        return userService.add(user).getBody();
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@RequestBody Long id, @Valid @RequestBody User user) {
        return userService.update(id, user);
    }

    @PostMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody Long id) {
        return userService.delete(id);
    }
}
