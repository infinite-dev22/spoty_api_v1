package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.SignUpModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupplierService {
    List<Supplier> getAll();

    Supplier getById(Long id) throws NotFoundException;

    List<Supplier> getByContains(String search);

    ResponseEntity<ObjectNode> save(Supplier supplier);

    ResponseEntity<ObjectNode> update(Supplier supplier) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);
}
