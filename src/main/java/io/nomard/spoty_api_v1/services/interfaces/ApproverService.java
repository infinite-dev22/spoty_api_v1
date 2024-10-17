package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Reviewer;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface ApproverService {
    ArrayList<Reviewer> getAll(int pageNo, int pageSize);

    Reviewer getById(Long id) throws NotFoundException;

    Reviewer getByUserId(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Reviewer reviewer);

    ResponseEntity<ObjectNode> delete(Long id);
}
