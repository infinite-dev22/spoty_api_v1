package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UnitOfMeasureService {
    List<UnitOfMeasure> getAll();

    UnitOfMeasure getById(Long id) throws NotFoundException;

    List<UnitOfMeasure> getByContains(String search);

    ResponseEntity<ObjectNode> save(UnitOfMeasure user);

    ResponseEntity<ObjectNode> update(Long id, UnitOfMeasure user);

    ResponseEntity<ObjectNode> delete(Long id);
}
