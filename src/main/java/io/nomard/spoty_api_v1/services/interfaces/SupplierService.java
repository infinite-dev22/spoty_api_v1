package io.nomard.spoty_api_v1.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.SupplierDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public interface SupplierService {
    Page<SupplierDTO> getAll(int pageNo, int pageSize);

    SupplierDTO getById(Long id) throws NotFoundException;

    List<SupplierDTO> getByContains(String search);

    ResponseEntity<ObjectNode> save(Supplier supplier);

    ResponseEntity<ObjectNode> update(Supplier supplier) throws NotFoundException;

    ResponseEntity<ObjectNode> delete(Long id);

    ResponseEntity<ObjectNode> deleteMultiple(ArrayList<Long> idList);
}
