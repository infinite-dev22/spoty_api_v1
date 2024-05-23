package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.User;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<User> getAll(int pageNo, int pageSize);

    User getById(Long id) throws NotFoundException;

    User getByEmail(String email) throws NotFoundException;

    List<User> getByContains(String search);

    ResponseEntity<ObjectNode> add(UserModel user) throws NotFoundException;

    ResponseEntity<ObjectNode> update(UserModel user) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
