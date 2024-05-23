package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Role;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface RoleService {
    List<Role> getAll(int pageNo, int pageSize);

    Role getById(Long id) throws NotFoundException;

    List<Role> search(String search);

    ResponseEntity<ObjectNode> save(Role role);

    ResponseEntity<ObjectNode> update(Role role) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
