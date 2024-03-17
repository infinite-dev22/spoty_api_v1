package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/profiles")
public class UserProfileController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/all")
    public List<User> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                             @RequestParam(defaultValue = "50") Integer pageSize) {
        return userService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public User getById(@RequestBody FindModel findModel) throws NotFoundException {
        return userService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<User> getByContains(@RequestBody SearchModel searchModel) {
        return userService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ObjectNode save(@Valid @RequestBody User user) throws NotFoundException {
        return userService.add(user).getBody();
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody User user) throws NotFoundException {
        return userService.update(user);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return userService.delete(findModel.getId());
    }
}
