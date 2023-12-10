package io.nomard.spoty_api_v1.services.interfaces;

import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(Long id) throws NotFoundException;

    User getByEmail(String email) throws NotFoundException;

    List<User> getByContains(String search);

    ResponseEntity<String> save(SignUpModel signUpDetails) throws NotFoundException;

    User update(Long id, User user);

    String delete(Long id);
}
