package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Permission;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface PermissionService {
    Page<Permission> getAll(int pageNo, int pageSize);

    Permission getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Permission permission);

    ResponseEntity<ObjectNode> update(Permission permission) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
