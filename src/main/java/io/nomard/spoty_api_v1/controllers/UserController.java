package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.models.UserModel;
import io.nomard.spoty_api_v1.services.implementations.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/all")
    public Flux<PageImpl<User>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return userService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<User> getById(@RequestBody FindModel findModel) {
        return userService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<List<User>> getByContains(@RequestBody SearchModel searchModel) {
        return userService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody UserModel userModel) {
        return userService.add(userModel);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody UserModel userModel) {
        return userService.update(userModel);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return userService.delete(findModel.getId());
    }
}
