package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Organisation;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrganisationService {
    List<Organisation> getAll();

    Organisation getById(Long id) throws NotFoundException;

    ResponseEntity<ObjectNode> save(Organisation organisation);

    ResponseEntity<ObjectNode> update(Organisation organisation) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
