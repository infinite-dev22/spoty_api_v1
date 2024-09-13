package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Approver;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface ApproverService {
    ArrayList<Approver> getAll(int pageNo, int pageSize);

    Approver getById(Long id) throws NotFoundException;

    Approver getByUserId(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Approver approver);

    ResponseEntity<ObjectNode> delete(Long id);
}
