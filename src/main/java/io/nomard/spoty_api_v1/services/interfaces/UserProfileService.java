package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UserProfile;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.UserModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserProfileService {
    List<UserProfile> getAll(int pageNo, int pageSize);

    UserProfile getById(Long id);

    UserProfile getByEmail(String email);

    List<UserProfile> getByContains(String search);

    ResponseEntity<ObjectNode> add(UserProfile userProfile);

    ResponseEntity<ObjectNode> update(UserModel userModel);

    ResponseEntity<ObjectNode> delete(Long id);
}
