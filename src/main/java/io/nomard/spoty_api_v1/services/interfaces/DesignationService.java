package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Designation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DesignationService {
    List<Designation> getAll(int pageNo, int pageSize);

    Designation getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Designation designation);

    ResponseEntity<ObjectNode> update(Designation designation) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(List<Long> idList) throws NotFoundException;
}
